package com.tutortekorg.tutortek.navigation_fragments.meetings

import android.app.Activity.RESULT_OK
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.ConfigurationCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.*
import com.tutortekorg.tutortek.R
import com.tutortekorg.tutortek.data.Meeting
import com.tutortekorg.tutortek.databinding.FragmentMeetingSignupBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MeetingSignupFragment : Fragment() {
    private lateinit var binding: FragmentMeetingSignupBinding
    private lateinit var meeting: Meeting
    private lateinit var paymentsClient: PaymentsClient
    private val merchantInfo = JSONObject().put("merchantName", "Tutortek")
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
    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }
    private val resolvePaymentForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> Toast.makeText(requireContext(), R.string.payment_success, Toast.LENGTH_SHORT).show()
        }
    }

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
        binding.btnGooglePay.root.setOnClickListener { requestPayment() }
        return binding.root
    }

    private fun preparePaymentClient() {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()
        paymentsClient = Wallet.getPaymentsClient(requireContext(), walletOptions)
    }

    private fun sendIsReadyToPayRequest() {
        val isReadyToPayJson = isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener {
            if (it.isSuccessful) binding.btnGooglePay.root.visibility = View.VISIBLE
        }
    }

    private fun gatewayTokenizationSpecification() = JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject(mapOf(
                "gateway" to "mpgs",
                "gatewayMerchantId" to "tutortek_id")))
        }

    private fun getTransactionInfo(price: String) = JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)
            put("countryCode", locale.country)
            put("currencyCode", "EUR")
        }

    private fun isReadyToPayRequest() = try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }
        } catch (e: JSONException) {
            null
        }

    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())
        return cardPaymentMethod
    }

    private fun getPaymentDataRequest(price: String): JSONObject? {
        try {
            return baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo(price))
                put("merchantInfo", merchantInfo)


                val shippingAddressParameters = JSONObject().apply {
                    put("phoneNumberRequired", false)
                    put("allowedCountryCodes", JSONArray(listOf("US", "GB", "LT")))
                }

                put("shippingAddressParameters", shippingAddressParameters)
                put("shippingAddressRequired", true)
            }
        } catch (e: JSONException) {
            return null
        }
    }

    private fun requestPayment() {
        binding.btnGooglePay.root.isClickable = false
        val paymentDataRequestJson = getPaymentDataRequest(meeting.price.toString()) ?: return
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = paymentsClient.loadPaymentData(request)
        task.addOnCompleteListener(requireActivity()) {
            if(it.isSuccessful) Toast.makeText(requireContext(), R.string.payment_success, Toast.LENGTH_SHORT).show()
            else when (val exception = it.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build())
                    }
                    else -> Toast.makeText(requireContext(), R.string.error_purchase, Toast.LENGTH_SHORT).show()
                }
        }
        binding.btnGooglePay.root.isClickable = true
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
