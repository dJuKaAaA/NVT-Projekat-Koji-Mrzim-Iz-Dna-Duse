import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { GraphComponent } from '../../../graph/graph.component';
import { dataSeries } from './data-series';
import { ActivatedRoute } from '@angular/router';
import { AmbientSensorService } from 'src/app/service/ambient-sensor.service';
import { AmbientSensorHistoryRequest } from 'src/app/model/ambient-sensor/request/ambient-sensor-history-request.model';
import { PredefinedHistoryPeriod } from 'src/app/model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum';
import { GraphDataSeries } from 'src/app/model/graph-data-series.model';
import { AmbientSensorSocketService } from 'src/app/service/socket/ambient-sensor-socket.service';
import { ToggleSwitchComponent } from 'src/app/components/toggle-switch/toggle-switch.component';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-ambient-sensor',
  templateUrl: './ambient-sensor-history.component.html',
  styleUrls: ['./ambient-sensor-history.component.css'],
})
export class AmbientSensorComponent implements OnInit, OnDestroy {

  @ViewChild('ambientSensorSwitch', { static: false }) switch!: ToggleSwitchComponent;
  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public selectedPeriod: string = "";
  public isDeviceOn:boolean = false;
  public currentTemperature:number = 0
  public currentAirHumidity:number = 0
  public deviceId:number;

  public startDateTime:String;
  public endDateTime:String;

  public temperatureDataSeries:GraphDataSeries[] = [];
  public humidityDataSeries:GraphDataSeries[] = [];

  constructor(
    private route: ActivatedRoute,
    private ambientSensorService:AmbientSensorService,
    private ambientSensorSocketService:AmbientSensorSocketService) {
    this.route.params.subscribe(params => {
      const id = params['deviceId'];
      this.deviceId = parseInt(id, 10)
    });
  }

  // TODO check if is deviceOn
  ngOnInit(): void {
    this.onOpenSocket();
  }

  private onOpenSocket() {
    this.ambientSensorSocketService.connect(this.deviceId, (response: string) => {

      const parsedObject = JSON.parse(response);
      this.isDeviceOn = true;

      this.currentTemperature = parsedObject.temperature.toFixed(2);
      this.currentAirHumidity = parsedObject.humidity.toFixed(2);

      let tempData: GraphDataSeries = {
        date: new Date(parsedObject.timestamp),
        value: parsedObject.temperature
      };
      console.log(tempData);

      let humidityData: GraphDataSeries = {
        date: new Date(parsedObject.timestamp),
        value: parsedObject.humidity
      };
      this.liveStream(tempData, humidityData);
    });
  }

  ngOnDestroy(): void {
    this.ambientSensorSocketService.closeConnection(this.deviceId, "Called ngOnDestroy of component");
  }

  onPeriodChange(event: any):void {
    this.currentTemperature = 0;
    this.currentAirHumidity = 0;
    this.selectedPeriod = event.target.value;
    this.temperatureDataSeries = []
    this.humidityDataSeries = []

    const temperatureGraph = this.graphComponents.get(0);
    const humidityGraph = this.graphComponents.get(1);

   temperatureGraph?.clear();
   humidityGraph?.clear();

    if(this.selectedPeriod != 'CUSTOM_DATE') {
      this.displayData();
      this.displayData()
    }
  }

  public displayNextGraph():void {
    const graphComponents = this.graphComponents;
    const totalGraphs = graphComponents.length;

    for (let i = 0; i < totalGraphs; i++) {
        const graph = graphComponents.get(i);
        const nextGraphIndex = (i + 1) % totalGraphs;

        if (graph && graph.visible === true) {
            graph.visible = false;

            const nextGraph = graphComponents.get(nextGraphIndex);

            if (nextGraphIndex === 0 && nextGraph && nextGraph.visible === false) {
                nextGraph.visible = true;
                return;
            }

            if (nextGraph) {
                nextGraph.visible = true;
                return;
            }
        }
    }
  }

  public toggleDevice(event:boolean):void {
    this.isDeviceOn = event
    if(this.isDeviceOn) {
      this.ambientSensorService.setActive(this.deviceId).subscribe(response => alert("Device is on!"),
      (error:any) => alert(error));
      this.onOpenSocket();
    }
    else {
      this.ambientSensorService.setInactive(this.deviceId).subscribe(response => alert("Device is off!"),
      (error:any) => alert(error));
      this.ambientSensorSocketService.closeConnection(this.deviceId, "Device is off!")
    }

  }

  public displayData() {

    let period:PredefinedHistoryPeriod = this.stringToEnum(this.selectedPeriod);
    let request:AmbientSensorHistoryRequest = {} as AmbientSensorHistoryRequest;

    if(period == 'CUSTOM_DATE') {
    console.log(this.startDateTime)

      request = {
        period:this.stringToEnum(this.selectedPeriod),
        startDateTime: this.startDateTime,
        endDateTime:this.endDateTime,
      };

    } else if(this.selectedPeriod == 'REAL_TIME') {

      request = {
        period:this.stringToEnum('H1'),
        startDateTime: '',
        endDateTime: '',
      };

    } else {
      request = {
        period:this.stringToEnum(this.selectedPeriod),
        startDateTime: null,
        endDateTime: null,
      }
    }

    this.ambientSensorService.getHistory(this.deviceId, request).subscribe({
      next: response => {
      response.forEach(el => {
      let timestamp = new Date(el.timestamp);
      console.log(timestamp);
      this.temperatureDataSeries.push({date:timestamp, value:el.temperature})
      this.humidityDataSeries.push({date:timestamp, value:el.humidity})

      this.temperatureDataSeries.sort((a, b) => a.date.getTime() - b.date.getTime())
      this.humidityDataSeries.sort((a, b) => a.date.getTime() - b.date.getTime())

      const temperatureGraph = this.graphComponents.get(0);
      const humidityGraph = this.graphComponents.get(1);

      temperatureGraph?.setData(this.temperatureDataSeries);
      humidityGraph?.setData(this.humidityDataSeries);
      temperatureGraph?.initChartData()
      humidityGraph?.initChartData();
      })
    },
    error: (error: HttpErrorResponse) => alert("Bad entered date!")
    });
  }

  public liveStream(temp:GraphDataSeries, humidity:GraphDataSeries) {
    let period:PredefinedHistoryPeriod = this.stringToEnum(this.selectedPeriod);
    if(period == 'REAL_TIME') {
      const temperatureGraph = this.graphComponents.get(0);
      const humidityGraph = this.graphComponents.get(1);
      temperatureGraph?.addAddData(temp);
      humidityGraph?.addAddData(humidity);
    }
  }

  private subtractHours(date:Date, hours:number) {
    date.setHours(date.getHours() - hours);
    return date;
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

  public displayOnDisplayGraphData() {
    this.displayData();
    this.displayData()
  }

  formatDate(date:Date) {
    let year = date.getFullYear();
    let month = (date.getMonth() + 1).toString().padStart(2, '0');
    let day = date.getDate().toString().padStart(2, '0');
    let hours = date.getHours().toString().padStart(2, '0');
    let minutes = date.getMinutes().toString().padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
}
