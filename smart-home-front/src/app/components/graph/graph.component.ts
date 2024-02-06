import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexTitleSubtitle,
  ApexDataLabels,
  ApexFill,
  ApexMarkers,
  ApexYAxis,
  ApexXAxis,
  ApexTooltip, ApexStroke,
} from 'ng-apexcharts';
import { GraphDataSeries } from 'src/app/model/graph-data-series.model';
import {formatDate} from "@angular/common";

@Component({
  selector: 'app-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.css'],
})
export class GraphComponent implements OnInit {
  public series: ApexAxisChartSeries = {} as ApexAxisChartSeries;
  public chart: ApexChart = {} as ApexChart;
  public dataLabels: ApexDataLabels = {} as ApexDataLabels;
  public markers: ApexMarkers = {} as ApexMarkers;
  public title: ApexTitleSubtitle = {} as ApexTitleSubtitle;
  public fill: ApexFill = {} as ApexFill;
  public yaxis: ApexYAxis = {} as ApexYAxis;
  public xaxis: ApexXAxis = {} as ApexXAxis;
  public tooltip: ApexTooltip = {} as ApexTooltip;
  public stroke: ApexStroke = {} as ApexStroke

  @Input() public graphTitle: string = '';
  @Input() public pointTitle: string = '';
  @Input() public yAxisTitle: string = '';
  @Input() public dataSeries:GraphDataSeries[] = []
  @Input() public visible:boolean = true;
  @Input() public height: number = 350
  @Input() public strokeType: "smooth" | "straight" | "stepline" | ("smooth" | "straight" | "stepline")[] = 'smooth'
  @Input() public yAxisFormatter = (val: any) => {
    return val.toFixed(1);
  };

  public dataToDisplay:any[] = []
  public xAxisOffsetInMilliseconds:number = 0

  ngOnInit(): void {
    this.initChartData();
  }

  public calculateDiffTimeStamp(currentPointDate: Date, previousPointDate: Date): number {
    return currentPointDate.getTime() - previousPointDate.getTime();
  }

  public initChartData(): void {
    this.dataToDisplay = []
    let previousPointDate: Date;
    let currentPointDate: Date;

    if(this.dataSeries.length > 0) {
      this.xAxisOffsetInMilliseconds = this.dataSeries[0].date.getTime();

      for (let i = 0; i < this.dataSeries.length; i++) {
        if (i != 0) {
          previousPointDate = this.dataSeries[i - 1].date;
          currentPointDate = this.dataSeries[i].date;
          const diffTimeStamps: number = this.calculateDiffTimeStamp(currentPointDate, previousPointDate);
          this.xAxisOffsetInMilliseconds += diffTimeStamps;
        }
        let value:number = this.dataSeries[i].value
        this.dataToDisplay.push([this.xAxisOffsetInMilliseconds, value]);
      }
    }


    this.series = [
      {
        name: this.pointTitle,
        data: this.dataToDisplay,
      },
    ];

    this.chart = {
      type: 'area',
      stacked: false,
      height: this.height,
      zoom: {
        type: 'x',
        enabled: true,
        autoScaleYaxis: true,
      },
      toolbar: {
        autoSelected: 'zoom',
      },
    };

    this.dataLabels = {
      enabled: false,
    };

    this.markers = {
      size: 0,
    };
    this.title = {
      text: this.graphTitle,
      align: 'left',
    };

    this.fill = {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        inverseColors: false,
        opacityFrom: 0.4,
        opacityTo: 0,
        stops: [0, 90, 100],
      },
    };

    this.yaxis = {
      labels: {
        formatter: this.yAxisFormatter,
      },
      title: {
        text: this.yAxisTitle,
      },
    };

    this.xaxis = {
      type: 'datetime',
      labels: {
        formatter: function (timestamp) {
          return formatDate(new Date(timestamp), 'dd-MM-yyyy HH:mm:ss', 'en_US');
        },
        style : { fontSize: '10px' }
      },
    };

    this.tooltip = {
      shared: false,
      y: {
        formatter: this.yAxisFormatter,
      },
    };

    this.stroke = {
      curve: this.strokeType,
      width: 3
    }
  }

  public toggleVisible():void {
    this.visible = !this.visible;
    console.log(this.visible);
  }

  public addAddData(dataPoint:GraphDataSeries) {
    this.dataSeries.push(dataPoint);


    let previousPointDate: Date;
    let currentPointDate: Date;

    if(this.dataSeries.length > 0) {

      let indexLastEl = this.dataSeries.length - 1;

      if(indexLastEl > 0) {
        previousPointDate = this.dataSeries[indexLastEl - 1].date;
        currentPointDate = this.dataSeries[indexLastEl].date;
        const diffTimeStamps: number = this.calculateDiffTimeStamp(currentPointDate, previousPointDate);
        this.xAxisOffsetInMilliseconds += diffTimeStamps;
      } else {
        this.xAxisOffsetInMilliseconds = dataPoint.date.getTime();
      }


      let value:number = this.dataSeries[indexLastEl].value
      this.dataToDisplay.push([this.xAxisOffsetInMilliseconds, value]);
      }

      this.series = [
      {
        name: this.pointTitle,
        data: this.dataToDisplay,
      },
    ];
  }

  public clear() {
    this.dataToDisplay = []
    this.xAxisOffsetInMilliseconds = 0
    this.dataSeries = []
    this.series = [
      {
        name: this.pointTitle,
        data: this.dataToDisplay,
      },
    ];
  }

  public setData(data:GraphDataSeries[]) {
    this.dataSeries = data;
  }
}
