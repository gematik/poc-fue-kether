/*
 * Copyright 2022-2024, gematik GmbH
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
 */
@ExperimentalSerializationApi
class EthTests {
    companion object {
        val account1 = accountStore.getAccount(AccountStore.TEST_ACCOUNT_1)
        val ethereum1 =  Eth(Rpc("http://besu.lab.gematik.de:8545"))
        val ethereum2 =  Eth(Rpc("http://besu.lab.gematik.de:8547"))
    }

    @Test
    fun ethBlockNumber() {
        val rpcResponse = ethereum1.ethBlockNumber()
        assert(rpcResponse > Quantity(0))
    }

    @Test
    fun ethChainId() {
        val rpcResponse = ethereum1.ethChainId()
        assert(rpcResponse > Quantity(0))
    }

    @Test
    fun ethGetBalance() {
        val rpcResponse = ethereum1.ethGetBalance(account1.address, Quantity(Tag.latest))
        assert(rpcResponse > Quantity(0))
    }

    @Test
    fun ethAccounts() {
        var rpcResponse = ethereum1.ethAccounts()
        assert(rpcResponse.isEmpty())
        rpcResponse = ethereum2.ethAccounts()
        assert(rpcResponse.isNotEmpty())
    }

    @Test
    fun ethGasPrice() {
        val rpcResponse = ethereum1.ethGasPrice()
        assert(rpcResponse >= Quantity(0))
    }

    @Test
    fun ethEstimateGas() {
        val rpcResponse = ethereum1.ethEstimateGas(
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