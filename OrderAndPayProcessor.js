function setUser(context, events, done) {
    const n = Math.floor(Math.random() * 50000) + 1;
    context.vars.loginId = "user-" + n;
    return done();
}

function setProduct(context, events, done) {
    context.vars.productId = Math.floor(Math.random() * 50000) + 1;
    return done();
}

function setOrder(context, events, done) {
    const uniqueId = context.vars.$uuid;
    context.vars.orderName = "order-" + uniqueId;
    context.vars.orderId = "order-" + uniqueId;
    return done();
}

function setPay(context, events, done) {
    const random = context.vars.$uuid;
    context.vars.paymentKey = "paymentKey-" + random;
    return done();
}

function logResponse(requestParams, response, context, ee, next) {
    console.log("STATUS:", response.statusCode);
    console.log("BODY:", response.body);
    return next();
}


module.exports = {
    setUser,
    setProduct,
    setOrder,
    setPay,
    logResponse
};