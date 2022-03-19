package com.tutortekorg.tutortek.navigation_fragments.meetings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.databinding.FragmentMeetingSignupBinding
import org.json.JSONArray
import org.json.JSONObject

class MeetingSignupFragment : Fragment() {
    private lateinit var binding: FragmentMeetingSignupBinding
    private lateinit var meeting: Meeting
    private lateinit var paymentsClient: PaymentsClient
    private val allowedCardAuthMethods = JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS"))
    private val allowedCardNetworks = JSONArray(listOf(
        "AMEX",
        "DISCOVER",
        "INTERAC",
        "JCB",
        "MASTERCARD",
        "MIR",
        "VISA")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeetingSignupBinding.inflate(inflater, container, false)
        meeting = arguments?.getSerializable("meeting") as Meeting
        binding.txtMeetingNamePay.text = getString(R.string.meeting_name_header, meeting.name)
        binding.txtMeetingPricePay.text = getString(R.string.meeting_price_header, meeting.price.toString())
        preparePaymentClient()
        sendIsReadyToPayRequest()
        return binding.root
    }

    private fun preparePaymentClient() {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
        paymentsClient = Wallet.getPaymentsClient(requireContext(), walletOptions)
    }

    private fun sendIsReadyToPayRequest() {
        val readyToPayRequest = IsReadyToPayRequest.fromJson(baseConfigurationJson().toString())
        val task = paymentsClient.isReadyToPay(readyToPayRequest)
        task.addOnCompleteListener {
            if (it.isSuccessful) {
                binding.btnGooglePay.root.visibility = View.VISIBLE
            }
        }
    }

    private fun baseConfigurationJson() = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
        }

    private fun baseCardPaymentMethod() = JSONObject().apply {
            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                })
            }

            put("type", "CARD")
            put("parameters", parameters)
        }
}
