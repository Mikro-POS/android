package com.herlianzhang.mikropos.vo

data class Stock(
    val id: Int,
    val productId: Int,
    val supplierName: String?,
    val amount: Int?,
    val soldAmount: Int?,
    val purchasePrice: Long?,
    val source: StockSource?,
    val supplier: Supplier?,
    val expiredDate: Long?,
    val createdAt: Long?,
) {
    val sourceString: String
        get() {
            return when (source) {
                StockSource.SUPPLIER -> "Pemasok"
                StockSource.CUSTOMER -> "Pengembalian Barang"
                else -> "Tidak Diketahui"
            }
        }
}