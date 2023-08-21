package fpt.aptech.portal.entities;

public class MoMoPaymentResponse {

    private String requestId;
    private int errorCode;
    private String orderId;
    private String message;
    private String localMessage;
    private String requestType;
    private String payUrl;
    private String signature;
    private String qrCodeUrl;
    private String deeplink;
    private String deeplinkWebInApp;

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getPartnerClientId() {
        return partnerClientId;
    }

    public void setPartnerClientId(String partnerClientId) {
        this.partnerClientId = partnerClientId;
    }
    
    private String partnerCode;
    private Long amount;
    private Long responseTime;
    private int resultCode;
    private String partnerClientId;
    // Các phương thức getter và setter cho các thuộc tính

    // Các phương thức getter và setter khác (nếu có)
    public boolean isSuccess() {
        return errorCode == 0;
    }
    
    public boolean isSuccess2() {
        return resultCode == 0;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocalMessage() {
        return localMessage;
    }

    public void setLocalMessage(String localMessage) {
        this.localMessage = localMessage;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getDeeplinkWebInApp() {
        return deeplinkWebInApp;
    }

    public void setDeeplinkWebInApp(String deeplinkWebInApp) {
        this.deeplinkWebInApp = deeplinkWebInApp;
    }
}
