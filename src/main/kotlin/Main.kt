import java.lang.Exception
import kotlin.random.Random

fun main(args: Array<String>) {
    //Instancio el juego
    var minesweeper = Minesweeper()

    //Pregunto por el número de minas que el usuario quiere poner en el campo
    var numero: Int? = null
    while (numero == null) {
        print("How many mines do you want on the field?")
        val input = readLine()
        try {
            numero = input?.toInt()
            if (numero != null && (numero < 1 || numero > 81)) {
                throw NumberFormatException()
            }
        } catch (e: NumberFormatException) {
            println("Error: Entrada inválida. Inténtelo nuevamente.")
            numero = null
        }
    }


    minesweeper.PonerMinas(numero)
    minesweeper.recorrerYPonerNumeros()
    minesweeper.SustituirPountosPorBarras()
   // minesweeper.MostrarPisicionMinas()// Eliminar
    minesweeper.MostrarTablero()



    while (true) {

        var cordx: Int
        var cordy: Int
        var command: String = ""
        while (true) {
            print("Set/unset mines marks or claim a cell as free: ")
            val input = readLine()
            try {
                val tokens = input?.split(" ")

                if (tokens?.size != 3) {
                    throw IllegalArgumentException()
                }
                cordx = tokens[0].toIntOrNull() ?: throw NumberFormatException()
                cordy = tokens[1].toIntOrNull() ?: throw NumberFormatException()
                command = tokens[2]
                if (command != "mine" && command != "free") {
                    throw IllegalArgumentException()
                }
                if (!(cordx in 1..9)) {
                    throw IllegalArgumentException()
                }
                if (!(cordy in 1..9)) {
                    throw IllegalArgumentException()
                }
                break // Salir del bucle si las coordenadas y el comando son válidos
            } catch (e: Exception) {
                println("Las coordenadas o comando ingresado no válidos. Inténtelo nuevamente.")
            }
        }
        cordx--
        cordy--


        if (command == "mine") {

            when (minesweeper.matrizParaMostrar[cordy][cordx]) {
                '.' -> {
                    minesweeper.PonerCaracterMatMostrar(cordx, cordy, '*')
                    //minesweeper.MostrarPisicionMinas()// Eliminar
                    minesweeper.MostrarTablero()
                }

                '*' -> {
                    minesweeper.PonerCaracterMatMostrar(cordx, cordy, '.')
                  //  minesweeper.MostrarPisicionMinas()// Eliminar
                    minesweeper.MostrarTablero()
                }


                /*'/' -> println("Es una barra")
                in '1'..'9' -> println("Es un dígito entre 1 y nueve")
                'X' -> println("Es una mina")
                '*' -> println("es un asterisco ")*/
            }
        }

        if (command == "free") {
            when (minesweeper.matrizPosicionMinas[cordy][cordx].toChar()) {
                '/' -> {//the cell is empty and has no mines around,
                   // println("Aqui barra")
                    minesweeper.ShowFreeSpacesAroundo(cordy, cordx)
                   // minesweeper.MostrarPisicionMinas()// Eliminar
                    minesweeper.MostrarTablero()
                }
                in '1'..'9' -> { //the cell is empty and has mines around
                    minesweeper.CopiarCaracter(cordx, cordy)
                   // minesweeper.MostrarPisicionMinas()// Eliminar
                    minesweeper.MostrarTablero()
                }

                'X' -> {//the explored cell contains a mine
                    minesweeper.DescubrirTodasLasMinas()
                   // minesweeper.MostrarPisicionMinas()// Eliminar
                    minesweeper.MostrarTablero()
                    println("You stepped on a mine and failed!")
                    break
                }

                else -> println("Error, no puede haber un caracter distinto ")
            }
        }


        if(minesweeper.CheckWin()){
            println("Congratulations! You found all the mines!")
            break
        }

    }
    //println("Fin del juego")
}

/**
 *
 * Crea la clase Minesweeper que contiene el tablero de 9*9 mas todas las propiedades y procedimientos
 *
 */


class Minesweeper {
    var matrizPosicionMinas: Array<Array<Char>> = Array(9) { Array(9) { '.' } }
    var matrizParaMostrar: Array<Array<Char>> = Array(9) { Array(9) { '.' } }

    // solo para probar el programa, no se usa en realidad
    fun MostrarPisicionMinas() {
        for (x in 0..8) {
            for (y in 0..8) {
                print(matrizPosicionMinas[x][y])
            }
            println()
        }
    }

    fun MostrarTablero() {
        println(" │123456789│")
        println("—│—————————│")
        for (x in 0..8) {
            print("${x + 1}│")
            for (y in 0..8) {
                print(matrizParaMostrar[x][y])
            }
            println("│")
        }
        println("—│—————————│")
    }

    fun PonerMinas(cant: Int) {
        var cont = 0
        while (cont < cant) {
            val x = Random.nextInt(9)
            val y = Random.nextInt(9)
            if (matrizPosicionMinas[x][y] == '.') {
                matrizPosicionMinas[x][y] = 'X'//  Se ponene las minas en el tablaro que no se muestra
                cont++;
            }
        }
    }

    fun MinesAraund(fila: Int, columna: Int): Int {
        val filas = matrizPosicionMinas.size
        val columnas = matrizPosicionMinas[0].size
        var contadorX = 0
        for (i in -1..1) {
            for (j in -1..1) {
                val nuevaFila = fila + i
                val nuevaColumna = columna + j
                if (nuevaFila in 0 until filas && nuevaColumna in 0 until columnas && (i != 0 || j != 0)) {
                    if (matrizPosicionMinas[nuevaFila][nuevaColumna] == 'X') {
                        contadorX++
                    }
                }
            }
        }
        return contadorX
    }

    fun recorrerYPonerNumeros() {
        for (x in 0..8) {
            for (y in 0..8) {
                if (matrizPosicionMinas[x][y] == '.') {
                    var cant = MinesAraund(x, y)
                    if (cant > 0) {
                        matrizPosicionMinas[x][y] = cant.toString().first().toChar()
                    }
                }
            }
        }
    }

    fun SustituirPountosPorBarras() {
        for (x in 0..8) {
            for (y in 0..8) {
                if (matrizPosicionMinas[x][y] == '.') {
                    matrizPosicionMinas[x][y] = '/'
                }
            }
        }
    }

    fun DescubrirTodasLasMinas() {
        for (x in 0..8) {
            for (y in 0..8) {
                if (matrizPosicionMinas[x][y] == 'X') {
                    matrizParaMostrar[x][y] = 'X'
                }
            }
        }
    }


    /*
    copia un caracter según parametros de la matriz con la posicion de las minas a la matriz para mostrar
     */
    fun CopiarCaracter(x: Int, y: Int) {
        matrizParaMostrar[y][x] = matrizPosicionMinas[y][x]
    }

    fun PonerCaracterMatMostrar(x: Int, y: Int, car: Char) {
        matrizParaMostrar[y][x] = car


    }

    /**
     *
     * Esta funcion muestra todos los espacios consecutivos y los numeros alrededor a partir de unas coordenadas que representan un espacio
     * hace uso de reucursividad
     *
     */
    fun ShowFreeSpacesAroundo(fila: Int, columna: Int) {
        matrizParaMostrar[fila][columna] = '/'
        val filas = matrizPosicionMinas.size
        val columnas = matrizPosicionMinas[0].size
        for (i in -1..1) {
            for (j in -1..1) {
                val nuevaFila = fila + i
                val nuevaColumna = columna + j
                if (nuevaFila in 0 until filas && nuevaColumna in 0 until columnas && (i != 0 || j != 0)) {
                    if ((matrizPosicionMinas[nuevaFila][nuevaColumna] == '/') && (matrizParaMostrar[nuevaFila][nuevaColumna] != '/')) {
                        this.ShowFreeSpacesAroundo(nuevaFila, nuevaColumna)//Llamada recursiva
                    }
                    if ((matrizPosicionMinas[nuevaFila][nuevaColumna] in '1'..'9')){
                        this.CopiarCaracter(nuevaColumna, nuevaFila)
                        }
                }
            }
        }
    }


    fun SustituirMinsasPorPuntos() {
        for (x in 0..8) {
            for (y in 0..8) {
                if (matrizParaMostrar[x][y] == 'X') {
                    matrizParaMostrar[x][y] = '.'
                }
            }
        }
    }

    fun CheckWin(): Boolean {
        //compruebo si todos las casillas con minas tienen marcas
        var todaMinatieneMarca = true
        for (x in 0..8) {
            for (y in 0..8) {
                if (matrizPosicionMinas[x][y] == 'X') {
                    if (matrizParaMostrar[x][y] != '*') {
                        todaMinatieneMarca = false
                    }
                }
            }
        }
        //Compruebo si en todas las marcas hay minas
        var todaMaracaTieneMina = true
        for (x in 0..8) {
            for (y in 0..8) {
                if (matrizParaMostrar[x][y] == '*') {
                    if (matrizPosicionMinas[x][y] != 'X') {
                        todaMaracaTieneMina = false
                    }
                }
            }
        }
        return todaMinatieneMarca && todaMaracaTieneMina// si se cumplen las dos condiciones ha ganado
    }

}
