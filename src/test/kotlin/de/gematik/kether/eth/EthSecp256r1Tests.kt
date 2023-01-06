package de.gematik.kether.eth

import de.gematik.kether.abi.DataEncoder
import de.gematik.kether.contracts.Storage
import de.gematik.kether.crypto.AccountStore
import de.gematik.kether.crypto.accountStore
import de.gematik.kether.eth.types.*
import de.gematik.kether.rpc.Rpc
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.Test

/**
 * Created by rk on 02.08.2022.
 * gematik.de
 */
@ExperimentalSerializationApi
class EthSecp256r1Tests {
    companion object {
        val account1 = accountStore.getAccount(AccountStore.TEST_ACCOUNT_1_R)
        val besu1 =  Eth(Rpc("http://besu1.lab.gematik.de:8545"))
    }

    @Test
    fun ethBlockNumber() {
        val rpcResponse = besu1.ethBlockNumber()
        assert(rpcResponse > Quantity(0))
    }

    @Test
    fun ethChainId() {
        val rpcResponse = besu1.ethChainId()
        assert(rpcResponse > Quantity(0))
    }

    @Test
    fun ethGetBalance() {
        val rpcResponse = besu1.ethGetBalance(account1.address, Quantity(Tag.latest))
        assert(rpcResponse > Quantity(0))
    }

    @Test
    fun ethAccounts() {
        val rpcResponse = besu1.ethAccounts()
        assert(rpcResponse.isEmpty())
    }

    @Test
    fun ethGasPrice() {
        val rpcResponse = besu1.ethGasPrice()
        assert(rpcResponse >= Quantity(0))
    }

    @Test
    fun ethEstimateGas() {
        val rpcResponse = besu1.ethEstimateGas(
            Transaction(
                from = account1.address,
                data = DataEncoder()
                    .encode(Data4(Storage.functionStore))
                    .encode(Quantity(10))
                    .build()
            ),
        )
        assert(rpcResponse >= Quantity(0))
    }

}