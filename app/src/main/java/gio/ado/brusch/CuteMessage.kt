package gio.ado.brusch

data class CuteMessage(
    var message: String,
    var image: String? = null
){
    constructor() : this("") // Costruttore senza argomenti
}