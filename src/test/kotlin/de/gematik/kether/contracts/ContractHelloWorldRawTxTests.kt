package de.gematik.kether.contracts

import de.gematik.kether.eth.Eth
import de.gematik.kether.eth.types.Address
import de.gematik.kether.eth.types.SubscriptionTypes
import de.gematik.kether.eth.types.Transaction
import de.gematik.kether.rpc.Rpc
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.math.BigInteger
import java.util.*

/**
 * Created by rk on 02.08.2022.
 * gematik.de
 */
@ExperimentalSerializationApi
class ContractHelloWorldRawTxTests {

    companion object {
        val account2Address = Address("0xfe3b557e8fb62b89f4916b721be55ceb828dbd73")
        val account2PrivateKey = BigInteger("8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63", 16)

        lateinit var helloWorld: HelloWorld

        @BeforeAll
        @JvmStatic
        fun helloWorldDeploy() {
            runBlocking {
                val ethereum1 = Eth(Rpc("http://ethereum1.lab.gematik.de:8545", "ws://ethereum1.lab.gematik.de:8546"))
                val greet = "Hello World"
                val receipt = HelloWorld.deploy(ethereum1, account2Address, greet, account2PrivateKey)
                val helloWorldAddress = receipt.contractAddress!!
                assert(receipt.isSuccess)
                helloWorld = HelloWorld(
                    ethereum1,
                    Transaction(to = helloWorldAddress, from = account2Address),
                    account2PrivateKey
                )
            }
        }

        @AfterAll
        @JvmStatic
        fun cancelGldToken() {
            helloWorld.cancel()
        }
    }


    @Test
    fun helloWorldGreeting() {
        helloWorld.greeting()
    }

    @Test
    fun helloWorldNewGreeting() {
        runBlocking {
            val greeting = "Greetings at ${Date()}"
            launch {
                val receipt = helloWorld.newGreeting(_greet = greeting)
                assert(receipt.isSuccess)
            }
            launch {
                helloWorld.eth.ethSubscribe(SubscriptionTypes.logs)
                helloWorld.eth.notifications.collect {
                    val result = helloWorld.greeting()
                    assert(greeting == result)
                    helloWorld.cancel()
                    cancel()
                }
            }
        }
    }

}