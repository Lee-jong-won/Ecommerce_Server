let userSeq = 1;
let orderSeq = 1;

function setUser(context, events, done) {
    const n = Math.floor(Math.random() * 500) + 1;
    context.vars.loginId = "user-" + n;
    return done();
}

function setProduct(context, events, done) {
    context.vars.productId = Math.floor(Math.random() * 500) + 1;
    return done();
}

function setOrder(context, events, done) {
    const random = Math.random().toString(36).substring(7);
    context.vars.orderName = "order-" + random;
    context.vars.orderId = "order-" + random;
    return done();
}

function setPay(context, events, done) {
    const random = Math.random().toString(36).substring(7);
    context.vars.paymentKey = "paymentKey-" + random;
    return done();
}

function setSequentialIds(context, events, done) {
    context.vars.loginId = `user-${userSeq}`;
    context.vars.orderId = `orderId-${orderSeq}`;

    // 증가 + 500 넘으면 초기화
    userSeq = userSeq >= 500 ? 1 : userSeq + 1;
    orderSeq = orderSeq >= 500 ? 1 : orderSeq + 1;

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
    setSequentialIds,
    logResponse
};