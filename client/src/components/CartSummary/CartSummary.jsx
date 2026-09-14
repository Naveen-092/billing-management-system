import './CartSummary.css';
import upiQR from "../../assets/upi-qr.png";
import {useContext, useState} from "react";
import {AppContext} from "../../context/AppContext.jsx";
import ReceiptPopup from "../ReceiptPopup/ReceiptPopup.jsx";
import {createOrder, deleteOrder} from "../../Service/OrderService.js";
import toast from "react-hot-toast";
// import {createRazorpayOrder, verifyPayment} from "../../Service/PaymentService.js";
// import {AppConstants} from "../../util/constants.js";

const CartSummary = ({customerName, mobileNumber, setMobileNumber, setCustomerName}) => {
    const {cartItems, clearCart} = useContext(AppContext);

    const [isProcessing, setIsProcessing] = useState(false);
    const [orderDetails, setOrderDetails] = useState(null);
    const [showPopup, setShowPopup] = useState(false);
    const [showUPIPopup, setShowUPIPopup] = useState(false);

    const totalAmount = cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
    const tax = totalAmount * 0.01;
    const grandTotal = totalAmount + tax;

    const clearAll = () => {
        setCustomerName("");
        setMobileNumber("");
        clearCart();
    }

    const placeOrder = () => {
        setShowPopup(true);
        clearAll();
    }

    const handlePrintReceipt = () => {
        window.print();
    }

    // const loadRazorpayScript = () => {
    //     return new Promise((resolve, reject) => {
    //         const script = document.createElement('script');
    //         script.src = "https://checkout.razorpay.com/v1/checkout.js";
    //         script.onload = () => resolve(true);
    //         script.onerror = () => resolve(false);
    //         document.body.appendChild(script);
    //     })
    // }

    // const deleteOrderOnFailure = async (orderId) => {
    //     try {
    //         await deleteOrder(orderId);
    //     } catch (error) {
    //         console.error(error);
    //         toast.error("Something went wrong");
    //     }
    // }

const completePayment = async (paymentMode) => {
    if (!customerName || !mobileNumber) {
        toast.error("Please enter customer details");
        return;
    }

    if (cartItems.length === 0) {
        toast.error("Your cart is empty");
        return;
    }

    // For UPI, show QR code first
    if (paymentMode === "upi") {
        setShowUPIPopup(true);
        return;
    }

    const orderData = {
        customerName,
        phoneNumber: mobileNumber,
        cartItems,
        subtotal: totalAmount,
        tax,
        grandTotal,
        paymentMethod: paymentMode.toUpperCase()
    };

    setIsProcessing(true);

    try {
        const response = await createOrder(orderData);
        const savedData = response.data;

        if (response.status === 201 && paymentMode === "cash") {
            toast.success("Cash received");
            setOrderDetails(savedData);
        }

    } catch (error) {
        console.error(error);
        toast.error("Payment processing failed");
    } finally {
        setIsProcessing(false);
    }
};
const handleUPIPaymentCompleted = async () => {
    const orderData = {
        customerName,
        phoneNumber: mobileNumber,
        cartItems,
        subtotal: totalAmount,
        tax,
        grandTotal,
        paymentMethod: "UPI"
    };

    setIsProcessing(true);

    try {
        const response = await createOrder(orderData);

        if (response.status === 201) {
            toast.success("UPI payment recorded");
            setOrderDetails(response.data);
            setShowUPIPopup(false);
        }
    } catch (error) {
        console.error(error);
        toast.error("Unable to save order");
    } finally {
        setIsProcessing(false);
    }
};

    return (
        <div className="mt-2">
            <div className="cart-summary-details">
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-light">Item: </span>
                    <span className="text-light">₹{totalAmount.toFixed(2)}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-light">Tax (1%):</span>
                    <span className="text-light">₹{tax.toFixed(2)}</span>
                </div>
                <div className="d-flex justify-content-between mb-4">
                    <span className="text-light">Total:</span>
                    <span className="text-light">₹{grandTotal.toFixed(2)}</span>
                </div>
            </div>

            <div className="d-flex gap-3">
                <button className="btn btn-success flex-grow-1"
                    onClick={() => completePayment("cash")}
                        disabled={isProcessing}
                >
                    {isProcessing ? "Processing...": "Cash"}
                </button>
                <button className="btn btn-primary flex-grow-1"
                        onClick={() => completePayment("upi")}
                        disabled={isProcessing}
                >
                    {isProcessing ? "Processing...": "UPI"}
                </button>
            </div>
            <div className="d-flex gap-3 mt-3">
                <button className="btn btn-warning flex-grow-1"
                    onClick={placeOrder}
                    disabled={isProcessing || !orderDetails}
                >
                    Place Order
                </button>
            </div>
            {
                showPopup && (
                    <ReceiptPopup
                        orderDetails={{
                            ...orderDetails,
                            razorpayOrderId: orderDetails.paymentDetails?.razorpayOrderId,
                            razorpayPaymentId: orderDetails.paymentDetails?.razorpayPaymentId,
                        }}
                        onClose={() => setShowPopup(false)}
                        onPrint={handlePrintReceipt}
                    />
                    
                )
            }
            {
    showUPIPopup && (
        <div className="upi-modal-overlay">
            <div className="upi-modal">

                <button
                    className="upi-close-btn"
                    onClick={() => setShowUPIPopup(false)}
                    disabled={isProcessing}
                >
                    ×
                </button>

                <h2>UPI Payment</h2>

                <p className="upi-scan-text">
                    Scan the QR code to pay
                </p>

                <div className="upi-qr-container">
                    <img
                        src={upiQR}
                        alt="UPI QR Code"
                        className="upi-qr-image"
                    />
                </div>

                <div className="upi-amount">
                    <span>Amount</span>
                    <strong>₹{grandTotal.toFixed(2)}</strong>
                </div>

                <p className="upi-note">
                    Scan this QR code using any UPI application
                    such as Google Pay, PhonePe or Paytm.
                </p>

                <button
                    className="btn btn-success w-100 mt-3"
                    onClick={handleUPIPaymentCompleted}
                    disabled={isProcessing}
                >
                    {isProcessing
                        ? "Processing..."
                        : "Payment Completed"}
                </button>

                <button
                    className="btn btn-secondary w-100 mt-2"
                    onClick={() => setShowUPIPopup(false)}
                    disabled={isProcessing}
                >
                    Cancel
                </button>

            </div>
        </div>
    )
}
        </div>
    )
}

export default CartSummary;