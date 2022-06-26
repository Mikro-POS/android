package com.herlianzhang.mikropos.ui.customer.customerlist

import com.herlianzhang.mikropos.vo.Customer

sealed class CustomerListEvent {
    data class NavigateToCustomerDetail(val id: Int) : CustomerListEvent()
    data class BackWithResult(val customer: Customer) :CustomerListEvent()
}