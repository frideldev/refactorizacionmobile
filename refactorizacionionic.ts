@Component({
  selector: 'app-p',
  templateUrl: './p.component.html',
})
export class PComponent implements OnInit {
  i: any[] = [];
  t = 0;
  l = false;
  s = '';

  constructor(private h: HttpClient) {}

  ngOnInit() {
    this.l = true;
    this.h.get('https://api.example.com/data').subscribe(
      (d: any) => {
        this.i = d;
        this.c();
        this.l = false;
      },
      (e) => {
        console.log('Error', e);
        this.s = 'Error al cargar datos';
        this.l = false;
      }
    );
  }

  a(p) {
    this.i.push(p);
    this.c();
  }

  r(idx) {
    this.i.splice(idx, 1);
    this.c();
  }

  c() {
    this.t = 0;
    for (let j = 0; j < this.i.length; j++) {
      if (this.i[j].a === true) {
        this.t += this.i[j].p * this.i[j].q;
      }
    }
  }

  sv() {
    this.l = true;
    this.h.post('https://api.example.com/save', this.i).subscribe(
      () => {
        this.s = 'Guardado correctamente';
        this.l = false;
      },
      (e) => {
        this.s = 'Error al guardar';
        console.log('Error', e);
        this.l = false;
      }
    );
  }
}
