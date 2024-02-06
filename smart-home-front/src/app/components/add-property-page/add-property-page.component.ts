import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { Router } from '@angular/router';
import * as L from 'leaflet'
import { ImgRequest } from 'src/app/model/request/img-request.model';
import { CityRequestDto } from 'src/app/model/request/city-country-request.model';
import { PropertyRequestDto, PropertyType } from 'src/app/model/request/property-request.model';
import { PropertyResponseDto } from 'src/app/model/response/property-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { CityService } from 'src/app/service/city.service';
import { ImageService } from 'src/app/service/image.service';
import { PropertyService } from 'src/app/service/property.service';

@Component({
  selector: 'app-add-property-page',
  templateUrl: './add-property-page.component.html',
  styleUrls: ['./add-property-page.component.css']
})
export class AddPropertyPageComponent implements AfterViewInit {

  constructor(
    private imageService: ImageService,
    private cityService: CityService,
    private propertyService: PropertyService,
    private authService: AuthService,
    private router: Router) {
    this.cityService.getAll().subscribe({
      next: (cities: CityRequestDto[]) => {
        this.allCityCountryOptions = cities
        this.shownCityCountryOptions = cities
      },
      error: (error: HttpErrorResponse) => {
        console.log(error.message);
      }
    })
  }

  ngAfterViewInit(): void {
    this.map = L.map('map', { center: [45.245745, 19.849195], zoom: 15 })

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager_labels_under/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
    }).addTo(this.map)

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      if (this.marker) this.map?.removeLayer(this.marker)
      if (e.latlng) this.marker = L.marker(e.latlng, { icon: this.customIcon }).addTo(this.map!)
    })
    this.dialog = document.getElementById('dialog-send-request') as HTMLDialogElement
  }

  // map
  private map: L.Map | undefined;
  marker: L.Marker | undefined;
  private customIcon = L.divIcon({
    className: 'custom-icon',
    html: '<i class="material-symbols-outlined map-pin">home_pin</i>',
    iconSize: [15, 15]
  })

  // dropdown
  openedDropdown = false
  allCityCountryOptions: CityRequestDto[] = []
  shownCityCountryOptions: CityRequestDto[] = []
  selectedCity: any = null;
  onDropdownInputClick() { this.openedDropdown = !this.openedDropdown }
  onDropdownInputValueChange(e: any) {
    this.openedDropdown = true
    this.shownCityCountryOptions = this.allCityCountryOptions.filter(
      (s) => s.name.toUpperCase().includes(e.target.value.toUpperCase()) ||
      s.countryName.toUpperCase().includes(e.target.value.toUpperCase())
    )
  }
  onSelect(select: CityRequestDto) {
    this.form.get('propertyCityCountry')?.patchValue(select.name + ", " + select.countryName)
    this.openedDropdown = !this.openedDropdown
    this.selectedCity = select
  }
  loseFocus() {
    if (this.selectedCity == null) this.form.get("propertyCityCountry")?.setValue("")
    else this.form.get("propertyCityCountry")?.setValue(this.selectedCity.name + ", " + this.selectedCity.countryName)
  }

  //image
  public uploadedImage: File = {} as File;
  public imageUrl: string = '';
  public isImageUploaded: boolean = false;
  @ViewChild('fileInput') fileInput!: ElementRef;
  openFileInput() {this.fileInput.nativeElement.click();}
  public async onImageUpload(event: any) {
    this.uploadedImage = event.target.files[0]
    try {
      this.imageUrl = await this.imageService.convertImageForDisplayOnUpload(this.uploadedImage)
      this.isImageUploaded = true
      console.log(this.uploadedImage)
    } catch (error: any) { console.log('Uploud image failed.') }
  }

  // form
  showError = false
  form = new FormGroup({
    propertyName: new FormControl('', [Validators.required]),
    propertyFloors: new FormControl('', [Validators.required]),
    propertyArea: new FormControl('', [Validators.required]),
    propertyAddress: new FormControl('', [Validators.required]),
    propertyCityCountry: new FormControl('', [Validators.required]),
    propertyType: new FormControl('h', [Validators.required])
  })

  isInputErr(inputName: string) { return this.showError && this.form.get(inputName)?.invalid}
  async submit () {
    if(!this.form.valid || !this.isImageUploaded || this.marker == null) {
      this.showError = true
      return
    }
    else {
      this.showError = false
      let img: ImgRequest = await this.imageService.convertImageForSending(this.uploadedImage)
      let newProperty: PropertyRequestDto = {
        name: this.form.get('propertyName')?.value ?? '',
        ownerEmail: this.authService.getEmail(),
        floors: parseInt(this.form.get('propertyFloors')?.value ?? '1'),
        area: parseFloat(this.form.get('propertyArea')?.value ?? '1'),
        longitude: this.marker.getLatLng().lng,
        latitude: this.marker.getLatLng().lat,
        address: this.form.get('propertyAddress')?.value ?? '',
        type: this.form.get("propertyType")?.value === 'h' ? PropertyType.HOUSE : this.form.get("propertyType")?.value === 'a' ? PropertyType.APARTMENT : PropertyType.HOUSE,
        city: this.selectedCity,
        image: img
      }
      this.propertyService.sendRequest(newProperty).subscribe({
        next: (response: PropertyResponseDto) => console.log(response),
        error: (error: HttpErrorResponse) => console.log(error.message)
      })
      console.log(this.dialog);

      this.dialog?.showModal()
    }
  }

  dialog: HTMLDialogElement | null = null;
  closeDialog() {
    this.dialog?.close()
    this.router.navigate(['/properties'])
  }
}
