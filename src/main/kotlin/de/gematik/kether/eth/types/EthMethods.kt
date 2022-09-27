package de.gematik.kether.eth.types

/**
 * Created by rk on 02.08.2022.
 * gematik.de
 */
enum class EthMethods {
    eth_blockNumber,
    eth_chainId,
    eth_getBalance,
    eth_accounts,
    eth_gasPrice,
    eth_estimateGas,
    eth_call,
    eth_sendTransaction,
    eth_getTransactionReceipt,
    eth_subscribe,
    eth_unsubscribe
}