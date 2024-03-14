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

package de.gematik.kether.eth.types

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Created by rk on 02.08.2022.
 */

@ExperimentalSerializationApi
class QuantitySerializer : KSerializer<Quantity> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("QuantitySerializer", PrimitiveKind.STRING)

    @InternalSerializationApi
    override fun serialize(encoder: Encoder, value: Quantity) {
        val string = if (value.isTag()) value.toTag().name  else "0x${value.toBigInteger().toString(16)}"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Quantity {
        val string = decoder.decodeString()
        return if(string.lowercase().startsWith("0x")) {
            Quantity(string.drop(2).toBigInteger(16))
        } else {
            Quantity(Tag.valueOf(string))
        }
    }
}