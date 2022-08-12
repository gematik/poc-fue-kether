package de.gematik.kether.types

import de.gematik.kether.rpc.AnySerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Created by rk on 02.08.2022.
 * gematik.de
 */
@kotlinx.serialization.Serializable
@ExperimentalSerializationApi
class RpcRequest(val method: RpcMethods, val  params: List<@Serializable(with = AnySerializer::class)Any>){
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) var id: Int=0
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) val jsonrpc =  "2.0"
}