package com.hfad.palamarchuksuperapp.feature.bone.data

import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.AmountCurrencyEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.CashPaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ExchangeOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.OrderEntityWithServices
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.PaymentOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.SaleOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.ServiceOrderEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.OrderStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.PaymentStatisticEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.local.entities.statistics.SalesStatisticsEntity
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.CashPaymentOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.ExchangeOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.OrderStatisticsDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.PaymentOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.PaymentStatisticDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.SaleOrderDto
import com.hfad.palamarchuksuperapp.feature.bone.data.remote.dto.SalesStatisticsDto
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.AmountCurrency
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.CashPaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ExchangeOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.Order
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.OrderStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.PaymentStatistic
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SaleOrder
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.SalesStatistics
import com.hfad.palamarchuksuperapp.feature.bone.domain.models.ServiceOrder

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
    amountCurrency = order.amountCurrency.toDomain(),
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
        amountCurrency = amountCurrency.toEntity(),
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
        amountCurrency = amountCurrency.toEntity(),
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
    amountCurrency = amountCurrency.toDomain(),
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
    amountCurrency = amountCurrency.toEntity(),
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
    amountCurrency = amountCurrency.toEntity(),
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
    amountCurrency = amountCurrency.toDomain(),
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
    amountCurrency = amountCurrency.toEntity(),
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
    amountCurrency = amountCurrency.toEntity(),
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
    amountToExchange = amountToExchange.toDomain(),
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    amountCurrency = amountFromExchange.toDomain(),
    billingDate = billingDate,
    id = id,
    versionHash = versionHash
)

fun ExchangeOrder.toEntity(): ExchangeOrderEntity = ExchangeOrderEntity(
    id = id,
    amountToExchange = amountToExchange.toEntity(),
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    amountFromExchange = amountCurrency.toEntity(),
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
    amountToExchange = amountToExchange.toEntity(),
    typeToChange = typeToChange,
    date = date,
    transactionType = transactionType,
    amountFromExchange = amountCurrency.toEntity(),
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
    amountCurrency = amountCurrency.toDomain(),
    versionHash = versionHash
)

fun CashPaymentOrder.toEntity(): CashPaymentOrderEntity = CashPaymentOrderEntity(
    id = id,
    paymentNum = paymentNum,
    paymentSum = paymentSum,
    paymentDateCreation = paymentDateCreation,
    billingDate = billingDate,
    transactionType = transactionType,
    amountCurrency = amountCurrency.toEntity(),
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
    amountCurrency = amountCurrency.toEntity(),
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
    totalSalesAmount = totalSalesAmount.toDomain(),
    totalSalesNdsAmount = totalSalesNdsAmount.toDomain(),
    totalBuyers = totalBuyers
)

fun SalesStatistics.toEntity(): SalesStatisticsEntity = SalesStatisticsEntity(
    id = 1,
    totalSalesAmount = totalSalesAmount.toEntity(),
    totalSalesNdsAmount = totalSalesNdsAmount.toEntity(),
    totalBuyers = totalBuyers
)

fun SalesStatisticsDto.toEntity(): SalesStatisticsEntity = SalesStatisticsEntity(
    id = 1,
    totalSalesAmount = totalSalesAmount.toEntity(),
    totalSalesNdsAmount = totalSalesNdsAmount.toEntity(),
    totalBuyers = totalBuyers
)

fun PaymentStatisticEntity.toDomain(): PaymentStatistic = PaymentStatistic(
    totalPayment = totalPayment,
    totalReceiver = totalReceiver,
    daysToSend = daysToSend,
    paymentsByCurrency = paymentsByCurrency.map { it.toDomain() }
)

fun PaymentStatistic.toEntity(): PaymentStatisticEntity = PaymentStatisticEntity(
    id = 1,
    totalPayment = totalPayment,
    totalReceiver = totalReceiver,
    daysToSend = daysToSend,
    paymentsByCurrency = paymentsByCurrency.map { it.toEntity() }
)

fun PaymentStatisticDto.toDomain(): PaymentStatistic = PaymentStatistic(
    totalPayment = totalPayment,
    totalReceiver = totalReceiver,
    daysToSend = daysToSend,
    paymentsByCurrency = paymentsByCurrencyJson
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

fun AmountCurrency.toEntity(): AmountCurrencyEntity = AmountCurrencyEntity(
    amount = amount,
    currency = currency
)

fun AmountCurrencyEntity.toDomain(): AmountCurrency = AmountCurrency(
    amount = amount,
    currency = currency
)