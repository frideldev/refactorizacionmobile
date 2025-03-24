@Component({
  selector: 'app-p',
  templateUrl: './p.component.html',
})
export class PComponent implements OnInit {
  // Lista de elementos
  items: any[] = [];
  // Total calculado de los items activos
  total: number = 0;
  // Indicador de carga
  loading: boolean = false;
  // Mensaje de estado para el usuario
  statusMessage: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loading = true;
    this.fetchData();
  }

  
  //Obtiene los datos de la API y actualiza la lista de items.

  private fetchData(): void {
    this.http.get('https://api.example.com/data').subscribe(
      (response: any) => {
        this.items = response;
        this.calculateTotal();
        this.loading = false;
      },
      (error) => {
        console.error('Error al obtener datos:', error);
        this.statusMessage = 'Error al cargar datos';
        this.loading = false;
      }
    );
  }

  /**
   * Agrega un nuevo elemento a la lista y recalcula el total.
   * @param newItem Elemento a agregar.
   */
  addItem(newItem: any): void {
    this.items.push(newItem);
    this.calculateTotal();
  }

  /**
   * Elimina un elemento de la lista según el índice y recalcula el total.
   * @param index Índice del elemento a eliminar.
   */
  removeItem(index: number): void {
    this.items.splice(index, 1);
    this.calculateTotal();
  }

  /**
   * Calcula el total sumando (precio * cantidad) de los items activos.
   */
  private calculateTotal(): void {
    this.total = this.items.reduce((sum, item) => {
      return item.a ? sum + item.p * item.q : sum;
    }, 0);
  }

  /**
   * Guarda la lista de items en la API.
   */
  saveData(): void {
    this.loading = true;
    this.http.post('https://api.example.com/save', this.items).subscribe(
      () => {
        this.statusMessage = 'Guardado correctamente';
        this.loading = false;
      },
      (error) => {
        console.error('Error al guardar datos:', error);
        this.statusMessage = 'Error al guardar';
        this.loading = false;
      }
    );
  }
}
