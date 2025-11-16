package com.mds.sharedexpenses.data.repositories

// Add the functions that will communicate with the Firebase
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.models.Debt
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Transaction
import kotlin.collections.component1
import kotlin.collections.component2

class FIRGroupRepository(private val firebaseRepository: FirebaseRepository) {

    //Check if the current user is owner of the group
    fun checkOwners(data: Map<String,*>?): Boolean? {
        val currentUID = firebaseRepository.getCurrentUserUID()
        val usersMap = data?.get("users") as? Map<String, Any> ?: emptyMap()
        val currentUserData = usersMap[currentUID] as? Map<String, Any>
        return currentUserData?.get("owner") as? Boolean ?: false
    }
    fun toJsonGroup(group: Group): Map<String, *> {
        return mapOf("id" to group.id, "name" to group.name, "description" to group.description, "users" to group.users, "expenses" to group.expenses, "transactions" to group.transactions, "debts" to group.debts)
    }

    fun fromJsonGroup(data: Map<String, *>?): Group? {
        if (data == null) return null
        val is_Owner = checkOwners(data) ?: false
        val usersMap = data["users"] as? Map<String, Map<String, Any>> ?: emptyMap()
        val usersList = usersMap.map { (userId, userData) ->
            User(
                id = userId,
                name = userData["name"] as? String ?: "",
                email = userData["email"] as? String ?: "",
                groups = mutableListOf()
            )
        }?.toMutableList() ?: mutableListOf()

        val expensesMap = data["expenses"] as? Map<String, Map<String, Any>> ?: emptyMap()
        val expensesList = mutableListOf<Expense>()
        val expenseRepo = FIRExpenseRepository(firebaseRepository)
        for(expense in expensesMap.entries) {
            val expenseId = expense.key
            val expense = expenseRepo.fromJsonExpense(expensesMap, usersList, expenseId)
            if(expense != null) expensesList.add(expense)
        }

        val transactionsMap = data["transactions"] as? Map<String, Map<String, Any>> ?: emptyMap()
        val transactionsList = mutableListOf<Transaction>()
        val transactionRepo = FiRTransactionRepository(firebaseRepository)
        for(transaction in transactionsMap.entries) {
            val transactionId = transaction.key
            val transaction = transactionRepo.fromJsonTransaction(expensesMap, usersList, transactionId, expensesList)
            if(transaction != null) transactionsList.add(transaction)
        }

        val debtList = mutableListOf<Debt>()
        val debtRepo = FIRDebtRepository(firebaseRepository)
        for ((userId, userData) in usersMap) {
            val userDebtsMap = userData["debts"] as? Map<String, Map<String, Any>> ?: continue
            for ((debtId, debtData) in userDebtsMap) {
                val debt = debtRepo.fromJsonDebt(debtData, usersList, expensesList, debtId,
                    Group(
                        id = data["id"] as? String ?: "",
                        name = data["name"] as? String ?: "",
                        description = data["description"] as? String ?: ""
                    )
                )
                if (debt != null) debtList.add(debt)
            }
        }

        return Group(
            id = data["id"] as? String ?: return null,
            name = data["name"] as? String ?: "",
            description = data["description"] as? String ?: "",
            users = usersList,
            expenses = expensesList,
            transactions = transactionsList,
            debts = debtList,
            isOwner = is_Owner
        )
    }

    private suspend fun inviteUser(email: String): DataResult<Boolean> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "email" to email,
        )
        return firebaseRepository.callCloudFunction(FirebaseRepositoryImpl.inviteUserFunction, data)

    }

    public suspend fun notifyUserFromExpense(group:Group ,user:User, expense:Expense)  : DataResult<Boolean>{
        val associatedDebt = group.debts.firstOrNull { it.expenses.id == expense.id }
        if (associatedDebt == null) {return DataResult.Error("","") }
        val data = hashMapOf(
            "group" to group.id,
            "user" to user.id,
            "expense" to expense.id,
            "amount"  to associatedDebt.amount,
            "name" to expense.name
        )
        return firebaseRepository.callCloudFunction(FirebaseRepositoryImpl.notifyUserFunction, data)
    }
    
    //Here begins the getters
    //Get a group base on his id and fetch this data from the firebase
    suspend fun getGroupById(groupId: String): Group? {
        val groupRef = firebaseRepository.getGroupDirectory(groupId)
        val dataRes: DataResult<Map<String, Any>> = firebaseRepository.fetchDBRef(groupRef)
        if (dataRes is DataResult.Success) {
            return fromJsonGroup(dataRes.data)
        }
        return null
    }


    //Get the users according to the current group and return Map<userId, userName>
    suspend fun getUsersByGroup (group_id : String) : Map<String,*>? {
        val groupRepository = firebaseRepository.getGroupDirectory(group_id)
        val dataRes : DataResult<Map<String,*>> = firebaseRepository.fetchDBRef<Map<String,*>>(groupRepository)
        if (dataRes is DataResult.Success) {
            val usersMap = dataRes.data["users"] as? Map<String, Any> ?: emptyMap()
            val result = usersMap.mapValues { (_, userData) ->
                val userMap = userData as? Map<String, Any>
                userMap?.get("name") as? String ?: "" }
            return if (result.isEmpty()) null else result
            }
        else{return null}
    }

    //Create a group
    suspend fun createGroup(group : Group) : Boolean {
        val groupsDirectory = firebaseRepository.getGroupsDirectory()
        val result : DataResult<DatabaseReference> =  firebaseRepository.createChildReference(groupsDirectory)
        var newGroupDirectory : DatabaseReference? = null
        if (result is DataResult.Success){
            newGroupDirectory = result.data
        }
        else {
            return false
        }
        val jsonGroup : Map<String,*> = toJsonGroup(group)
        val dataRes : DataResult<Boolean> = firebaseRepository.writeToDBRef<Map<String,*>>(newGroupDirectory, jsonGroup)
        if(dataRes is DataResult.Success) {
            return true
        }
        else{
            return false
        }
    }

    //Get the expenses according to the current group and return Map<ExpId, Expense>
    suspend fun getExpensesForGroup(groupId: String): Map<String, Expense>? {
        val groupRef = firebaseRepository.getGroupDirectory(groupId)
        val dataRes: DataResult<Map<String, *>> = firebaseRepository.fetchDBRef(groupRef)
        if (dataRes is DataResult.Success) {
            val expensesMap = dataRes.data["expenses"] as? Map<String, Map<String, Any>> ?: return null
            val usersMap = dataRes.data["users"] as? Map<String, Map<String, Any>> ?: emptyMap()
            val usersList = usersMap.map { (uid, uData) ->
                User(
                    id = uid,
                    name = uData["name"] as? String ?: "",
                    email = uData["email"] as? String ?: "",
                    groups = mutableListOf()
                )
            }

            return expensesMap.mapValues { (_, expData) ->
                val payerId = expData["payer"] as? String ?: ""
                val payerUser = usersList.firstOrNull { it.id == payerId } ?: User(id = payerId, name = "", email = "", groups = mutableListOf())
                val debtorIds = expData["debtors"] as? List<String> ?: emptyList()
                val debtorUsers = debtorIds.map { did ->
                    usersList.firstOrNull { it.id == did } ?: User(id = did, name = "", email = "", groups = mutableListOf()) }.toMutableList()
                Expense(
                    id = expData["id"] as? String ?: "",
                    payer = payerUser,
                    debtors = debtorUsers,
                    amount = (expData["amount"] as? Number)?.toDouble() ?: 0.0,
                    name = expData["name"] as? String ?: "",
                    description = expData["description"] as? String ?: ""
                )
            }
        }
        return null
    }

    //Get the transactions according to the current group and return Map<ExpId, Transactions>
    suspend fun getTransactionsbyGroup(groupId : String) : Map<String, Transaction>? {
        val groupRef = firebaseRepository.getGroupDirectory(groupId)
        val dataRes: DataResult<Map<String, *>> = firebaseRepository.fetchDBRef(groupRef)
        if (dataRes is DataResult.Success) {
            val transactionssMap = dataRes.data["transactions"] as? Map<String, Map<String, Any>> ?: return null
            val expensesMap = getExpensesForGroup(groupId) ?: emptyMap()
            val usersMap = dataRes.data["users"] as? Map<String, Map<String, Any>> ?: emptyMap()
            val usersList = usersMap.map { (uid, uData) ->
                User(
                    id = uid,
                    name = uData["name"] as? String ?: "",
                    email = uData["email"] as? String ?: "",
                    groups = mutableListOf()
                )
            }
            return transactionssMap.mapValues { (_,transacData) ->
                var expenseId = transacData["expense_id"] as? String ?: ""
                val linkedExpense = expensesMap[expenseId] ?: Expense(id = expenseId, payer = User(id = "", name = "", email = "", groups = mutableListOf()), amount = 0.0, debtors = mutableListOf())
                val issuerId = transacData["issuer"] as? String ?: ""
                val issuerser = usersList.firstOrNull { it.id == issuerId } ?: User(id = issuerId, name = "", email = "", groups = mutableListOf())
                val receiverId = transacData["receiver"] as? String ?: ""
                val receiverUser = usersList.firstOrNull { it.id == receiverId } ?: User(id = receiverId, name = "", email = "", groups = mutableListOf())
                Transaction(
                    id = transacData["id"] as? String ?: "",
                    expense = linkedExpense,
                    amount = (transacData["amount"] as? Number)?.toDouble() ?: 0.0,
                    issuer = issuerser,
                    receiver = receiverUser
                )
            }
        }
        return null
    }

    //Calculate the nets solde to see if a user is creditor or debtor within a group
    fun getUserBalanceInGroup(transactions: List<Transaction>, userId: String): Double{
        var balance = 0.0
        for(transaction in transactions) {
            if(userId == transaction.id) balance += transaction.amount
            if(userId == transaction.id) balance -= transaction.amount
        }
        return balance
    }

    //Get a specific expense in the current group
    suspend fun getExpensebyId(groupId: String, expenseId: String): Expense? {
        val expenses = getExpensesForGroup(groupId)
        return expenses?.get(expenseId)
    }

    //Get the expenses for a user
    suspend fun getExpenseforUser(groupId: String, userId: String): List<Expense>? {
        val expenses = getExpensesForGroup(groupId)?.values ?: return emptyList()
        return expenses.filter{it.payer.id == userId || it.debtors.any{debtor -> debtor.id == userId}}
    }

    //Get a specific transaction in the current group
    suspend fun getTransactionbyId(groupId : String, transactionId : String): Transaction? {
        val transactions = getTransactionsbyGroup(groupId)
        return transactions?.get(transactionId)
    }

    //Get the transactions for a user
    suspend fun getTransactionsforUser(groupId : String, userId : String): List<Transaction>? {
        val transactions = getTransactionsbyGroup(groupId)?.values ?: return emptyList()
        return transactions.filter{it.issuer.id == userId || it.receiver.id == userId}
    }

    //Get all the transactions link to an expense
    suspend fun getTransactionsForExpense(groupId: String, expenseId: String): List<Transaction> {
        val transactions = getTransactionsbyGroup(groupId)?.values ?: return emptyList()
        return transactions.filter { it.expense.id == expenseId }
    }
}