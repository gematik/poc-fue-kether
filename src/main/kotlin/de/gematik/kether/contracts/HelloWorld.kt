import de.gematik.kether.abi.AbiBytes32
import de.gematik.kether.abi.AbiString
import de.gematik.kether.abi.DataDecoder
import de.gematik.kether.abi.DataEncoder
import de.gematik.kether.contracts.Contract
import de.gematik.kether.extensions.hexToByteArray
import de.gematik.kether.rpc.Eth
import de.gematik.kether.types.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class HelloWorld(
    eth: Eth,
    baseTransaction: Transaction = Transaction()
) : Contract(eth, baseTransaction) {

    companion object {
        val byteCode = "0x608060405234801561001057600080fd5b506040516108a53803806108a58339818101604052602081101561003357600080fd5b810190808051604051939291908464010000000082111561005357600080fd5b8382019150602082018581111561006957600080fd5b825186600182028301116401000000008211171561008657600080fd5b8083526020830192505050908051906020019080838360005b838110156100ba57808201518184015260208101905061009f565b50505050905090810190601f1680156100e75780820380516001836020036101000a031916815260200191505b50604052505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550806001908051906020019061014492919061014b565b50506101f6565b828054600181600116156101000203166002900490600052602060002090601f01602090048101928261018157600085556101c8565b82601f1061019a57805160ff19168380011785556101c8565b828001600101855582156101c8579182015b828111156101c75782518255916020019190600101906101ac565b5b5090506101d591906101d9565b5090565b5b808211156101f25760008160009055506001016101da565b5090565b6106a0806102056000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c806341c0e1b5146100465780634ac0d66e14610050578063ef690cc01461010b575b600080fd5b61004e61018e565b005b6101096004803603602081101561006657600080fd5b810190808035906020019064010000000081111561008357600080fd5b82018360208201111561009557600080fd5b803590602001918460018302840111640100000000831117156100b757600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050919291929050505061024b565b005b6101136104fb565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610153578082015181840152602081019050610138565b50505050905090810190601f1680156101805780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610232576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260228152602001806106496022913960400191505060405180910390fd5b3373ffffffffffffffffffffffffffffffffffffffff16ff5b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146102ef576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260228152602001806106496022913960400191505060405180910390fd5b806040518082805190602001908083835b602083106103235780518252602082019150602081019050602083039250610300565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040518091039020600160405180828054600181600116156101000203166002900480156103ad5780601f1061038b5761010080835404028352918201916103ad565b820191906000526020600020905b815481529060010190602001808311610399575b505091505060405180910390207f047dcd1aa8b77b0b943642129c767533eeacd700c7c1eab092b8ce05d2b2faf560018460405180806020018060200183810383528581815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561046b5780601f106104405761010080835404028352916020019161046b565b820191906000526020600020905b81548152906001019060200180831161044e57829003601f168201915b5050838103825284818151815260200191508051906020019080838360005b838110156104a557808201518184015260208101905061048a565b50505050905090810190601f1680156104d25780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a380600190805190602001906104f792919061059d565b5050565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105935780601f1061056857610100808354040283529160200191610593565b820191906000526020600020905b81548152906001019060200180831161057657829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f0160209004810192826105d3576000855561061a565b82601f106105ec57805160ff191683800117855561061a565b8280016001018555821561061a579182015b828111156106195782518255916020019190600101906105fe565b5b509050610627919061062b565b5090565b5b8082111561064457600081600090555060010161062c565b509056fe4f6e6c79206f776e65722063616e2063616c6c20746869732066756e6374696f6e2ea26469706673582212204fa584d190ef27198db31fdb0fe87244be87f144f2ffb5fa1d95a0f88796aae564736f6c63430007060033".hexToByteArray()
        val functionNewGreeting = "newGreeting(string)".keccak().copyOfRange(0, 4)
        val functionGreeting = "greeting()".keccak().copyOfRange(0, 4)
        val functionKill = "kill()".keccak().copyOfRange(0, 4)
        val eventModified = "Modified(string,string,string,string)".keccak()
        fun deploy(eth: Eth, from: Address, _greet: String): TransactionReceipt? {
            return runBlocking {
                eth.ethSendTransaction(
                    Transaction(
                        from = from,
                        gas = Quantity(1000000),
                        value = Quantity(0),
                        gasPrice = Quantity(0),
                        data = Data(byteCode + DataEncoder( )
                            .encode(_greet)
                            .build().value)
                    )
                ).result?.let {
                    val subscription = eth.ethSubscribe(SubscriptionTypes.newHeads).result!!
                    eth.notifications.first{it.params.subscription == subscription}
                    eth.ethUnsubscribe(subscription)
                    eth.ethGetTransactionReceipt(it).result
                }
            }
        }
    }

    // events
    // Modified(string,string,string,string)
    data class EventModified(
        val eventSelector: AbiBytes32,
        val oldGreetingIdx: AbiBytes32,
        val newGreetingIdx: AbiBytes32,
        val oldGreeting: AbiString,
        val newGreeting: AbiString
    ) : Event(topics = listOf(eventSelector, oldGreetingIdx, newGreetingIdx))

    private val decoderEventModified = { log: Log ->
        checkEvent(log, eventModified)?.let {
            val decoder = DataDecoder(log.data!!)
            val oldGreeting = decoder.next<AbiString>()
            val newGreeting = decoder.next<AbiString>()
            EventModified(
                eventSelector = log.topics!!.get(0).value,
                oldGreetingIdx = log.topics.get(1).value,
                newGreetingIdx = log.topics.get(2).value,
                oldGreeting = oldGreeting,
                newGreeting = newGreeting
            )
        }
    }

    override val listOfEventDecoders: List<(Log) -> Event?> = listOf(
        decoderEventModified
    )

    //functions
    // greeting():(string)
    data class ResultsGreeting(
        val value: AbiString
    )

    fun greeting(): ResultsGreeting {
        return eth.ethCall(
            baseTransaction.copy(
                data = DataEncoder()
                    .encodeSelector(functionGreeting)
                    .build()
            ),
            Quantity(Block.latest.value)
        ).result!!.let {
            ResultsGreeting(DataDecoder(it).next())
        }
    }

    // kill()
    suspend fun kill() {
        return withTimeout(10000){
            eth.ethSendTransaction(
                baseTransaction.copy(
                    data = DataEncoder()
                        .encodeSelector(functionKill)
                        .build()
                )
            ).let {
                it.result ?: throw Exception(it.error?.message?:"undefined error")
                val subscription = eth.ethSubscribe(SubscriptionTypes.newHeads).result ?: throw Exception("subscription id missing")
                eth.notifications.first { it.params.subscription == subscription}
                eth.ethUnsubscribe(subscription)
                val receipt = it.result?.let{
                    eth.ethGetTransactionReceipt(it)
                }
                if(receipt?.result?.status?.value != 1L) throw Exception(it.error?.message?:"undefined error")
            }
        }
    }

    // newGreeting(string)
    fun newGreeting(greeting: AbiString) {
        eth.ethSendTransaction(
            baseTransaction.copy(
                data = DataEncoder()
                    .encodeSelector(functionNewGreeting)
                    .encode(greeting)
                    .build()
            )
        )
    }

}
