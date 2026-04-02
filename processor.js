function setUser(context, events, done) {
    const n = Math.floor(Math.random() * 1000) + 1;
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

module.exports = {
    setUser,
    setProduct,
    setOrder
};