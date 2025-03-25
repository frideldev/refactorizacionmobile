import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

//Luciana Ichazo, Ana Gabriel

interface Producto {
  activo: boolean;
  precio: number;
  cantidad: number;
}

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
})
export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  total = 0;
  cargando = false;
  mensajeEstado = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.cargarProductos();
  }

  cargarProductos() {
    this.cargando = true;
    this.http.get<Producto[]>('https://api.example.com/data').subscribe(
      (datos) => {
        this.productos = datos;
        this.calcularTotal();
      },
      (error) => this.manejarError('Error al cargar los datos', error)
    ).add(() => this.cargando = false);
  }

  agregarProducto(producto: Producto) {
    this.productos.push(producto);
    this.calcularTotal();
  }

  eliminarProducto(indice: number) {
    this.productos.splice(indice, 1);
    this.calcularTotal();
  }

  calcularTotal() {
    this.total = this.productos.reduce((suma, producto) => 
      producto.activo ? suma + producto.precio * producto.cantidad : suma, 0);
  }

  guardarProductos() {
    this.cargando = true;
    this.http.post('https://api.example.com/save', this.productos).subscribe(
      () => this.mensajeEstado = 'Guardado correctamente',
      (error) => this.manejarError('Error al guardar', error)
    ).add(() => this.cargando = false);
  }

  private manejarError(mensaje: string, error: any) {
    console.error(mensaje, error);
    this.mensajeEstado = mensaje;
  }
}
