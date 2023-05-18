# Banking
List of api's
1) Create account
2) Withdraw
3) Deposit
4) Transaction
5) Check Balance
6) Check transactions between 2 particular dates
7) Login
8) Logout

I have created an banking application where users can deposit, withdraw and transfer their amount. And sessions have been created to maintain stateful information between multiple requests for the same user and helps in provide a way to store user-specific data on the server-side and associate it with a particular user's interactions with the application.
Admin's are also created and they have rights to check previos transaction of any particular customer.
Mailing service is also implemented to send mails when cash has been transfered, withdrawen or deposited.

In this project, instead of creating accountNumbers, email is considered as AccountId as it will be unique to every users.

Structure of the Project:
UserModel : It has the credentials and roles of the users
BalanceModel: It has data's where a user deposits or withdraws money
TransactionModel: When a transaction is taken place, it stores sender, receiver and amount being transfered along with time.
UserDataModel: It shows the amount every customer has. 
