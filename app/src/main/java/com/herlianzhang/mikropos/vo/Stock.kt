package com.herlianzhang.mikropos.vo

data class Stock(
    val id: Int,
    val productId: Int,
    val supplierName: String?,
    val amount: Int?,
    val soldAmount: Int?,
    val purchasePrice: Int?,
    val source: StockSource?,
    val supplier: Supplier?,
    val createdAt: Long?,
) {
    val sourceString: String
        get() {
            return when (source) {
                StockSource.SUPPLIER -> "Pemasok"
                StockSource.CUSTOMER -> "Pengembalian Dana"
                else -> "Tidak Diketahui"
            }
        }
}