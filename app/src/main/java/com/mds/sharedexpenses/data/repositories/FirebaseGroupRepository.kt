package com.mds.sharedexpenses.data.repositories

// Add the functions that will communicate with the Firebase
import com.google.firebase.database.DatabaseReference
import com.mds.sharedexpenses.data.utils.DataResult
import com.mds.sharedexpenses.domain.repository.FirebaseRepository
import com.mds.sharedexpenses.data.models.Group
import com.mds.sharedexpenses.data.models.User
import com.mds.sharedexpenses.data.models.Expense
import com.mds.sharedexpenses.data.models.Transaction
import kotlin.collections.component1
import kotlin.collections.component2

class FirebaseGroupRepository(private val firebaseRepository: FirebaseRepository) {

    //Check if the current user is owner of the group
    fun checkOwners(data: Map<String,*>?): Boolean? {
        val currentUID = firebaseRepository.getCurrentUserUID()
        val usersMap = data?.get("users") as? Map<String, Any> ?: emptyMap()
        val currentUserData = usersMap[currentUID] as? Map<String, Any>
        return currentUserData?.get("owner") as? Boolean ?: false
    }
    fun toJson(group: Group): Map<String, *> {
        return mapOf("id" to group.id, "name" to group.name, "description" to group.description, "users" to group.users, "expenses" to group.expenses, "transactions" to group.transactions, "debts" to group.debts)
    }

    fun fromJson(data: Map<String, *>?): Group? {
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
        val expensesList = expensesMap.map { (expenseId, expData) ->
            val payerId = expData["payer"] as? String ?: ""
            val payerUser = usersList.firstOrNull { user -> user.id == payerId } ?: User(id = payerId, name = "", email = "", groups = mutableListOf())
            val debtorIds = expData["debtors"] as? List<String> ?: emptyList()
            val debtorUsers = debtorIds.map { debtorId -> usersList.firstOrNull { user -> user.id == debtorId } ?: User(id = debtorId, name = "", email = "", groups = mutableListOf()) }.toMutableList()
            Expense(
                id = expenseId,
                payer = payerUser,
                amount = 0.0,
                debtors = debtorUsers,
                name = expData["name"] as? String ?: ""
            )
        }?.toMutableList() ?: mutableListOf()

        val transactionsMap = data["transactions"] as? Map<String, Map<String, Any>> ?: emptyMap()
        val transactionsList = transactionsMap.map { (transacId, transacData) ->
            val expId = transacData["expense_id"] as? String ?: ""
            val linkExpense = expensesList.firstOrNull { expense -> expense.id == expId } ?: Expense(id = expId, payer = usersList.first(), amount = 0.0, debtors = mutableListOf())
            val issuerId = transacData["issuer"] as? String ?: ""
            val issuerUser = usersList.firstOrNull { user -> user.id == issuerId } ?: User(id = issuerId, name = "", email = "", groups = mutableListOf())
            val receiverId = transacData["issuer"] as? String ?: ""
            val receiverUser = usersList.firstOrNull { user -> user.id == receiverId } ?: User(id = receiverId, name = "", email = "", groups = mutableListOf())
            Transaction(
                id = transacId,
                expense = linkExpense,
                amount = (transacData["amount"] as? Number)?.toDouble() ?: 0.0,
                issuer = issuerUser,
                receiver = receiverUser
            )
        }?.toMutableList() ?: mutableListOf()

        return Group(
            id = data["id"] as? String ?: return null,
            name = data["name"] as? String ?: "",
            description = data["description"] as? String ?: "",
            users = usersList,
            expenses = expensesList,
            transactions = transactionsList,
            isOwner = is_Owner
        )
    }

    //Here begins the getters


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
        val jsonGroup : Map<String,*> = toJson(group)
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
                val issuerId = transacData["payer"] as? String ?: ""
                val issuerser = usersList.firstOrNull { it.id == issuerId } ?: User(id = issuerId, name = "", email = "", groups = mutableListOf())
                val receiverId = transacData["payer"] as? String ?: ""
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
}