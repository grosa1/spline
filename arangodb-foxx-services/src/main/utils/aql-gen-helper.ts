/*
 * Copyright 2022 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ReadTxInfo } from '../persistence/model'
import { aql } from '@arangodb'


export class AQLCodeGenHelper {
    private i = 0

    constructor(
        private readonly rtxInfo: ReadTxInfo
    ) {
    }

    genTxIsolationCodeForTraversal(...aqlVarIdentifiers: string[]): ArangoDB.Query {
        return this.genTxIsolationCode(true, ...aqlVarIdentifiers)
    }

    genTxIsolationCodeForLoop(...aqlVarIdentifiers: string[]): ArangoDB.Query {
        return this.genTxIsolationCode(false, ...aqlVarIdentifiers)
    }

    private genTxIsolationCode(isTraversal: boolean, ...aqlVarIdentifiers: string[]): ArangoDB.Query {
        if (aqlVarIdentifiers.length === 0) {
            return aql``
        }
        this.i++
        const pruneVar = aql.literal(`__isAnyUncommitted_${this.i}`)
        const args = aql.literal(aqlVarIdentifiers.join(', '))
        const pruneOrLet = aql.literal(isTraversal ? 'PRUNE' : 'LET')
        return aql`
            ${pruneOrLet} ${pruneVar} = LENGTH([${args}][*
                FILTER CURRENT._tx_info.num >= ${this.rtxInfo}.num
                    OR POSITION(${this.rtxInfo}.liveTxIds, CURRENT._tx_info.uid)
                LIMIT 1
                RETURN CURRENT._id
            ]) != 0
            FILTER !${pruneVar}
        `
    }
}
