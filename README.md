## Centralized Blockchain
The project contains all the features before the advanced part.

*Brief comments have also been added to the top of each class to explain how it works.*

### Server

The server provides three services to miners.

- An initialization service to which the miner can connect to request the current blockchain from the server.
*Instead of initializing the blockchain in the miner, the miner requests the full blockchain from the server. Which allows the miner to log in later*

- A multicast service to which the miner can subscribe to constantly listen to the new blocks inserted.
*Multicast has been implemented using a `Tcp` socket because the messages sent are important, and it is necessary to ensure their arrival*

- A transaction verification service to which the miner can send a transaction with a salt to verify and broadcast it.

To simulate the verification time of a transaction. a command ``` Thread.sleep(1000); ``` has been added in the verification task. To allow testing the case where the miner does not wait for the response from the server.
### Customer

The client sends a transaction to a chosen miner.
The client must insert the transaction information (amount, payer, receiver) and the port of the chosen minor.
The client can send multiple transactions without waiting for the miner's response.
If the client sends an invalid transaction, it receives an invalid transaction message. (A payer or receiver that does not exist, or the payer does not have the amount)
### Minor

The miner constantly receives transactions to validate from customers.

The miner has two rows. A mining queue to find a salt for a transaction (`miningExceutor`), and another queue to verify the salt found by communicating with the server (`validationExceutor`).

The miner has two blockchains. A channel that is added only by transactions received from the multicast channel (verified by the server). And a local channel.

When the miner finds a new salt. It adds a new junction to the local chain, puts the transaction in the verification queue, and chains the local chain. In case the miner receives a refusal from the server, he stops all work to find a salt. It resets the local chain to the other chain and forwards all tasks from the validation queue to the mining queue. Because all the junctions in the commit queue were built based on the junction that was refused by the server.

The tasks are transmitted from one queue to another in such a way as to exploit the miner to the maximum but at the same time without losing any of them and without passing any of them without being verified in the server.

In the code there is no local string, it is only a pointer to the last junction. The principle is to have a chain in which we can trust and another chain which contains uncommitted transactions.

So that the miner does not wait for the response from the server, he tries to increase the difficulty in the same way as the server.


When starting a miner, the miner displays its port assigned by the operating system.


## Launch the project

In order to simplify the launch of the project.
`jar` have been added with the project.

To test the project.
Start by starting the server `java -cp DistributedProgramming.jar cryptocurrency.server.Server`
Then start one or more miner `java -cp DistributedProgramming.jar cryptomonaie.mining.Minor`
*Remember the port displayed in the minor*
Next, run a `java -cp DistributedProgramming.jar cryptocurrency.client.Client` client and follow the prompt to send a transaction.


## Sharing of tasks carried out within the pair.
We did not divide tasks between us.
Most of the time we met to work together in order to move forward faster.
