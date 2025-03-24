import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

/**
 * Clase que representa un producto.
 */
class Producto {
  constructor(
    public nombre: string,
    public precio: number,
    public cantidad: number,
    public seleccionado: boolean = false
  ) {}
}

/**
 * Componente que gestiona la lista de productos.
 */
@Component({
  selector: 'app-pagina',
  templateUrl: './pagina.component.html',
})
export class PaginaComponent implements OnInit {
  productos: Producto[] = []; // Lista de productos cargados
  totalSeleccionado: number = 0; // Total calculado de los productos seleccionados
  cargando: boolean = false; // Indicador de carga
  mensajeEstado: string = ''; // Mensaje de estado para el usuario

  private readonly apiUrl = 'https://api.example.com'; // URL base de la API

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  /**
   * Carga los productos desde la API y actualiza la lista de productos.
   */
  private loadProducts(): void {
    this.setLoadingState(true);
    this.httpClient.get<Producto[]>(`${this.apiUrl}/productos`).subscribe(
      (productos) => this.handleProductLoadSuccess(productos),
      (error) => this.handleProductLoadError(error)
    );
  }

  /**
   * Maneja la respuesta exitosa de la carga de productos.
   * @param productos Lista de productos cargados.
   */
  private handleProductLoadSuccess(productos: Producto[]): void {
    this.productos = productos;
    this.calculateTotalSelected();
    this.setLoadingState(false);
  }

  /**
   * Maneja el error al cargar productos.
   * @param error Error recibido.
   */
  private handleProductLoadError(error: any): void {
    console.error('Error al cargar productos', error);
    this.setMessage('Error al cargar productos');
    this.setLoadingState(false);
  }

  /**
   * Agrega un nuevo producto a la lista y recalcula el total.
   * @param producto El producto a agregar.
   */
  addProduct(producto: Producto): void {
    this.productos.push(producto);
    this.calculateTotalSelected();
  }

  /**
   * Elimina un producto de la lista por su índice y recalcula el total.
   * @param indice Índice del producto a eliminar.
   */
  removeProduct(indice: number): void {
    this.productos.splice(indice, 1);
    this.calculateTotalSelected();
  }

  /**
   * Calcula el total de los productos seleccionados en la lista.
   */
  private calculateTotalSelected(): void {
    this.totalSeleccionado = this.productos
      .filter(this.isProductSelected)
      .reduce(this.sumProductTotal, 0);
  }

  /**
   * Verifica si un producto está seleccionado.
   * @param producto Producto a verificar.
   * @returns Verdadero si el producto está seleccionado, falso en caso contrario.
   */
  private isProductSelected(producto: Producto): boolean {
    return producto.seleccionado;
  }

  /**
   * Suma el total de un producto.
   * @param suma Total acumulado.
   * @param producto Producto a sumar.
   * @returns Nuevo total acumulado.
   */
  private sumProductTotal(suma: number, producto: Producto): number {
    return suma + producto.precio * producto.cantidad;
  }

  /**
   * Guarda los productos en la API y muestra un mensaje de estado.
   */
  saveProducts(): void {
    this.setLoadingState(true);
    this.httpClient.post(`${this.apiUrl}/guardar`, this.productos).subscribe(
      () => this.handleSaveSuccess(),
      (error) => this.handleSaveError(error)
    );
  }

  /**
   * Maneja la respuesta exitosa al guardar productos.
   */
  private handleSaveSuccess(): void {
    this.setMessage('Productos guardados correctamente');
    this.setLoadingState(false);
  }

  /**
   * Maneja el error al guardar productos.
   * @param error Error recibido.
   */
  private handleSaveError(error: any): void {
    console.error('Error al guardar productos', error);
    this.setMessage('Error al guardar productos');
    this.setLoadingState(false);
  }

  /**
   * Establece el estado de carga.
   * @param estado Estado de carga.
   */
  private setLoadingState(estado: boolean): void {
    this.cargando = estado;
  }

  /**
   * Establece un mensaje de estado.
   * @param mensaje Mensaje a establecer.
   */
  private setMessage(mensaje: string): void {
    this.mensajeEstado = mensaje;
  }
}