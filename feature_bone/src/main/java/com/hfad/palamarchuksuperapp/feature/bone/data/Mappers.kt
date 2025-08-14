package com.hfad.palamarchuksuperapp.feature.bone.data

import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.*
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.*
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.*

fun OrderEntityWithServices.toDomain(): Order = Order(
    id = order.id,
    businessEntityNum = order.businessEntityNum,
    num = order.num,
    serviceList = services.map { it.toDomain() },
    status = order.status,
    destinationPoint = order.destinationPoint,
    arrivalDate = order.arrivalDate,
    containerNumber = order.containerNumber,
    departurePoint = order.departurePoint,
    cargo = order.cargo,
    manager = order.manager,
    amountCurrency = AmountCurrency(order.currency, order.sum),
    billingDate = order.billingDate,
    transactionType = order.transactionType,
    versionHash = order.versionHash
)

fun Order.toEntity(): OrderEntityWithServices = OrderEntityWithServices(
    order = OrderEntity(
        id = id,
        businessEntityNum = businessEntityNum,
        num = num,
        status = status,
        destinationPoint = destinationPoint,
        arrivalDate = arrivalDate,
        containerNumber = containerNumber,
        departurePoint = departurePoint,
        cargo = cargo,
        manager = manager,
        sum = amountCurrency.amount,
        currency = amountCurrency.currency,
        billingDate = billingDate,
        transactionType = transactionType,
        versionHash = versionHash
    ),
    services = serviceList.map { it.toEntity(id) }
)

fun OrderDto.toDomain(): Order = Order(
    id = id,
    businessEntityNum = businessEntityNum,
    num = num,
    serviceList = serviceList,
    status = status,
    destinationPoint = destinationPoint,
    arrivalDate = arrivalDate,
    containerNumber = containerNumber,
    departurePoint = departurePoint,
    cargo = cargo,
    manager = manager,
    amountCurrency = amountCurrency,
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

fun OrderDto.toEntity(): OrderEntityWithServices = OrderEntityWithServices(
    order = OrderEntity(
        id = id,
        businessEntityNum = businessEntityNum,
        num = num,
        status = status,
        destinationPoint = destinationPoint,
        arrivalDate = arrivalDate,
        containerNumber = containerNumber,
        departurePoint = departurePoint,
        cargo = cargo,
        manager = manager,
        sum = amountCurrency.amount,
        currency = amountCurrency.currency,
        billingDate = billingDate,
        transactionType = transactionType,
        versionHash = versionHash
    ),
    services = serviceList.map { it.toEntity(id) }
)

fun Order.toDto(): OrderDto = OrderDto(
    id = id,
    businessEntityNum = businessEntityNum,
    num = num,
    serviceList = serviceList,
    status = status,
    destinationPoint = destinationPoint,
    arrivalDate = arrivalDate,
    containerNumber = containerNumber,
    departurePoint = departurePoint,
    cargo = cargo,
    manager = manager,
    amountCurrency = amountCurrency,
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

// ServiceOrder
fun ServiceOrderEntity.toDomain(): ServiceOrder = ServiceOrder(
    id = id,
    orderId = orderId,
    fullTransport = fullTransport,
    serviceType = serviceType,
    price = price,
    durationDay = durationDay,
    status = status
)

fun ServiceOrder.toEntity(orderId: Int? = null): ServiceOrderEntity = ServiceOrderEntity(
    id = id,
    orderId = orderId ?: this.orderId,
    fullTransport = fullTransport,
    serviceType = serviceType,
    price = price,
    durationDay = durationDay,
    status = status
)

// PaymentOrder
fun PaymentOrderEntity.toDomain(): PaymentOrder = PaymentOrder(
    id = id,
    factory = factory,
    productType = productType,
    paymentDate = paymentDate,
    dueDate = dueDate,
    status = status,
    commission = commission,
    transactionType = transactionType,
    billingDate = billingDate,
    amountCurrency = AmountCurrency(amountCurrency, paymentSum),
    paymentPrice = AmountCurrency(amountCurrency, paymentSum),
    versionHash = versionHash
)

fun PaymentOrder.toEntity(): PaymentOrderEntity = PaymentOrderEntity(
    id = id,
    factory = factory,
    productType = productType,
    paymentDate = paymentDate,
    dueDate = dueDate,
    status = status,
    commission = commission,
    transactionType = transactionType,
    billingDate = billingDate,
    paymentSum = amountCurrency.amount,
    amountCurrency = amountCurrency.currency,
    versionHash = versionHash
)

fun PaymentOrderDto.toDomain(): PaymentOrder = PaymentOrder(
    id = id,
    factory = factory,
    productType = productType,
    paymentDate = paymentDate,
    dueDate = dueDate,
    status = status,
    commission = commission,
    transactionType = transactionType,
    billingDate = billingDate,
    amountCurrency = amountCurrency,
    paymentPrice = paymentPrice,
    versionHash = versionHash
)

fun PaymentOrderDto.toEntity(): PaymentOrderEntity = PaymentOrderEntity(
    id = id,
    factory = factory,
    productType = productType,
    paymentDate = paymentDate,
    dueDate = dueDate,
    status = status,
    commission = commission,
    transactionType = transactionType,
    billingDate = billingDate,
    paymentSum = amountCurrency.amount,
    amountCurrency = amountCurrency.currency,
    versionHash = versionHash
)

fun PaymentOrder.toDto(): PaymentOrderDto = PaymentOrderDto(
    id = id,
    factory = factory,
    productType = productType,
    paymentDate = paymentDate,
    dueDate = dueDate,
    status = status,
    commission = commission,
    transactionType = transactionType,
    billingDate = billingDate,
    amountCurrency = amountCurrency,
    paymentPrice = paymentPrice,
    versionHash = versionHash
)

// SaleOrder
fun SaleOrderEntity.toDomain(order: Order? = null): SaleOrder = SaleOrder(
    id = id,
    productName = productName,
    cargoCategory = cargoCategory,
    customerName = customerName,
    status = status,
    requestDate = requestDate,
    documentDate = documentDate,
    companyName = companyName,
    commissionPercent = commissionPercent,
    prepayment = prepayment,
    order = order,
    vat = vat,
    amountCurrency = AmountCurrency(currency, sum),
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

fun SaleOrder.toEntity(orderId: Int? = null): SaleOrderEntity = SaleOrderEntity(
    id = id,
    productName = productName,
    cargoCategory = cargoCategory,
    customerName = customerName,
    status = status,
    requestDate = requestDate,
    documentDate = documentDate,
    companyName = companyName,
    commissionPercent = commissionPercent,
    prepayment = prepayment,
    orderId = orderId,
    vat = vat,
    sum = amountCurrency.amount,
    currency = amountCurrency.currency,
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

fun SaleOrderDto.toDomain(): SaleOrder = SaleOrder(
    id = id,
    productName = productName,
    cargoCategory = cargoCategory,
    customerName = customerName,
    status = status,
    requestDate = requestDate,
    documentDate = documentDate,
    companyName = companyName,
    commissionPercent = commissionPercent,
    prepayment = prepayment,
    order = order,
    vat = vat,
    amountCurrency = amountCurrency,
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

fun SaleOrderDto.toEntity(): SaleOrderEntity = SaleOrderEntity(
    id = id,
    productName = productName,
    cargoCategory = cargoCategory,
    customerName = customerName,
    status = status,
    requestDate = requestDate,
    documentDate = documentDate,
    companyName = companyName,
    commissionPercent = commissionPercent,
    prepayment = prepayment,
    orderId = null, // DTO doesn't have orderId, it has order object
    vat = vat,
    sum = amountCurrency.amount,
    currency = amountCurrency.currency,
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

fun SaleOrder.toDto(): SaleOrderDto = SaleOrderDto(
    id = id,
    productName = productName,
    cargoCategory = cargoCategory,
    customerName = customerName,
    status = status,
    requestDate = requestDate,
    documentDate = documentDate,
    companyName = companyName,
    commissionPercent = commissionPercent,
    prepayment = prepayment,
    order = order,
    vat = vat,
    amountCurrency = amountCurrency,
    billingDate = billingDate,
    transactionType = transactionType,
    versionHash = versionHash
)

// ExchangeOrder
fun ExchangeOrderEntity.toDomain(): ExchangeOrder = ExchangeOrder(
    amountToExchange = AmountCurrency(currencyToChange, sumToExchange),
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    amountCurrency = AmountCurrency(exchangedCurrency, exchangedSum),
    billingDate = billingDate,
    id = id,
    versionHash = versionHash
)

fun ExchangeOrder.toEntity(): ExchangeOrderEntity = ExchangeOrderEntity(
    id = id,
    sumToExchange = amountToExchange.amount,
    currencyToChange = amountToExchange.currency,
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    exchangedSum = amountCurrency.amount,
    exchangedCurrency = amountCurrency.currency,
    billingDate = billingDate,
    versionHash = versionHash
)

fun ExchangeOrderDto.toDomain(): ExchangeOrder = ExchangeOrder(
    amountToExchange = amountToExchange,
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    amountCurrency = amountCurrency,
    billingDate = billingDate,
    id = id,
    versionHash = versionHash
)

fun ExchangeOrderDto.toEntity(): ExchangeOrderEntity = ExchangeOrderEntity(
    id = id,
    sumToExchange = amountToExchange.amount,
    currencyToChange = amountToExchange.currency,
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    exchangedSum = amountCurrency.amount,
    exchangedCurrency = amountCurrency.currency,
    billingDate = billingDate,
    versionHash = versionHash
)

fun ExchangeOrder.toDto(): ExchangeOrderDto = ExchangeOrderDto(
    amountToExchange = amountToExchange,
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    billingDate = billingDate,
    id = id,
    amountCurrency = amountCurrency,
    versionHash = versionHash
)

// CashPaymentOrder
fun CashPaymentOrderEntity.toDomain(): CashPaymentOrder = CashPaymentOrder(
    id = id,
    paymentNum = paymentNum,
    paymentSum = paymentSum,
    paymentDateCreation = paymentDateCreation,
    billingDate = billingDate,
    transactionType = transactionType,
    amountCurrency = AmountCurrency(currency, amount),
    versionHash = versionHash
)

fun CashPaymentOrder.toEntity(): CashPaymentOrderEntity = CashPaymentOrderEntity(
    id = id,
    paymentNum = paymentNum,
    paymentSum = paymentSum,
    paymentDateCreation = paymentDateCreation,
    billingDate = billingDate,
    transactionType = transactionType,
    amount = amountCurrency.amount,
    currency = amountCurrency.currency,
    versionHash = versionHash
)

fun CashPaymentOrderDto.toDomain(): CashPaymentOrder = CashPaymentOrder(
    id = id,
    paymentNum = paymentNum,
    paymentSum = paymentSum,
    paymentDateCreation = paymentDateCreation,
    billingDate = billingDate,
    transactionType = transactionType,
    amountCurrency = amountCurrency,
    versionHash = versionHash
)

fun CashPaymentOrderDto.toEntity(): CashPaymentOrderEntity = CashPaymentOrderEntity(
    id = id,
    paymentNum = paymentNum,
    paymentSum = paymentSum,
    paymentDateCreation = paymentDateCreation,
    billingDate = billingDate,
    transactionType = transactionType,
    amount = amountCurrency.amount,
    currency = amountCurrency.currency,
    versionHash = versionHash
)

fun CashPaymentOrder.toDto(): CashPaymentOrderDto = CashPaymentOrderDto(
    id = id,
    paymentNum = paymentNum,
    paymentSum = paymentSum,
    paymentDateCreation = paymentDateCreation,
    billingDate = billingDate,
    transactionType = transactionType,
    amountCurrency = amountCurrency,
    versionHash = versionHash
)

// Statistics mappings
fun SalesStatisticsEntity.toDomain(): SalesStatistics = SalesStatistics(
    totalSalesAmount = AmountCurrency(totalSalesAmountCurrency, totalSalesAmount),
    totalSalesNdsAmount = AmountCurrency(totalSalesNdsAmountCurrency, totalSalesNdsAmount),
    totalBuyers = totalBuyers
)

fun SalesStatistics.toEntity(): SalesStatisticsEntity = SalesStatisticsEntity(
    id = 1,
    totalSalesAmount = totalSalesAmount.amount,
    totalSalesAmountCurrency = totalSalesAmount.currency,
    totalSalesNdsAmount = totalSalesNdsAmount.amount,
    totalSalesNdsAmountCurrency = totalSalesNdsAmount.currency,
    totalBuyers = totalBuyers
)

fun PaymentStatisticEntity.toDomain(): PaymentStatistic = PaymentStatistic(
    totalPayment = totalPayment,
    totalReceiver = totalReceiver,
    daysToSend = daysToSend,
    paymentsByCurrency = emptyList() // TODO: Parse JSON string
)

fun PaymentStatistic.toEntity(): PaymentStatisticEntity = PaymentStatisticEntity(
    id = 1,
    totalPayment = totalPayment,
    totalReceiver = totalReceiver,
    daysToSend = daysToSend,
    paymentsByCurrencyJson = "" // TODO: Convert to JSON string
)

fun OrderStatisticsEntity.toDomain(): OrderStatistics = OrderStatistics(
    inProgressOrders = inProgressOrders,
    completedOrders = completedOrders,
    totalOrderWeight = totalOrderWeight
)

fun OrderStatistics.toEntity(): OrderStatisticsEntity = OrderStatisticsEntity(
    id = 1,
    inProgressOrders = inProgressOrders,
    completedOrders = completedOrders,
    totalOrderWeight = totalOrderWeight
)

fun OrderStatisticsDto.toDomain(): OrderStatistics = OrderStatistics(
    inProgressOrders = inProgressOrders,
    completedOrders = completedOrders,
    totalOrderWeight = totalOrderWeight
)

fun OrderStatistics.toDto(): OrderStatisticsDto = OrderStatisticsDto(
    inProgressOrders = inProgressOrders,
    completedOrders = completedOrders,
    totalOrderWeight = totalOrderWeight
)

fun OrderStatisticsDto.toEntity(): OrderStatisticsEntity = OrderStatisticsEntity(
    id = 1,
    inProgressOrders = inProgressOrders,
    completedOrders = completedOrders,
    totalOrderWeight = totalOrderWeight
) 