@Component({
  selector: 'app-p',
  templateUrl: './p.component.html',
})
// 
export class ItemComponent implements OnInit {
  items: any[] = [];
  total: number= 0;
  isLoading: boolean = false;
  statusMessage: string = '';

  constructor(private http: HttpClient) {}

  //Funcion que sirve para inicializar el componente
  //Realiza un http.get para obtener los datos de la API y actualiza el estado del componente
  ngOnInit() {
    this.isLoading = true;
    this.http.get('https://api.example.com/data').subscribe(
      (data: any) => {
        this.items = data;
        this.calculateTotal();
        this.isLoading = false;
      },
      (error) => {
        console.log('Error', error);
        this.statusMessage = 'Error al cargar datos';
        this.isLoading = false;
      }
    );
  }

  //Funcion para añadir un item al carrito


  addItem(item) {
    this.items.push(item);
    this.calculateTotal();
  }

//Funcion para eliminar un item del carrito
  removeItem(index: number) {
    this.items.splice(index, 1);
    this.calculateTotal();
  }

//Funcion para calcular el total del carrito
  calculateTotal() {
    this.total = this.items.reduce((sum, item) => {
      return item.isActive ? sum + item.price * item.quantity : sum;
    }, 0);
  }

  // Realiza una solicitud HTTP POST y actualiza el estado del componente basado en la respuesta.
  saveTimes() {
    this.isLoading = true;
    this.http.post('https://api.example.com/save', this.isLoading).subscribe(
      () => {
        this.statusMessage = 'Guardado correctamente';
        this.isLoading = false;
      },
      (error) => {
        this.statusMessage = 'Error al guardar';
        console.log('Error', error);
        this.isLoading = false;
      }
    );
  }
}
