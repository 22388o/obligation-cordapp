package flows.v2

import com.google.common.collect.ImmutableList
import contract.states.v2.ObligationV2
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction

/**
 * An abstract FlowLogic class that is subclassed by the obligation flows to
 * provide helper methods and classes.
 */
abstract class ObligationBaseFlow : FlowLogic<SignedTransaction>() {

    val firstNotary
        get() = serviceHub.networkMapCache.notaryIdentities.firstOrNull()
                ?: throw FlowException("No available notary.")

    fun getObligationByLinearId(linearId: UniqueIdentifier): StateAndRef<ObligationV2> {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(
                null,
                ImmutableList.of(linearId),
                Vault.StateStatus.UNCONSUMED, null)

        return serviceHub.vaultService.queryBy<ObligationV2>(queryCriteria).states.singleOrNull()
                ?: throw FlowException("Obligation with id $linearId not found.")
    }

    fun resolveIdentity(abstractParty: AbstractParty): Party {
        return serviceHub.identityService.requireWellKnownPartyFromAnonymous(abstractParty)
    }
}

internal class SignTxFlowNoChecking(otherFlow: FlowSession) : SignTransactionFlow(otherFlow) {
    override fun checkTransaction(tx: SignedTransaction) {
        // TODO: Add checking here.
    }
}