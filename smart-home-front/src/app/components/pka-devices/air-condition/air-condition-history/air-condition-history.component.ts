import { Component, OnInit, ViewChild } from '@angular/core';
import { AirConditionHistoryResponse } from 'src/app/model/response/air-condition-history-response.model';
import { AirConditionFilterComponent } from '../air-condition-filter/air-condition-filter.component';
import { ActivatedRoute } from '@angular/router';
import { AirConditionerHistoryResponse } from 'src/app/model/air_conditioner/response/air-conditioner-history-respnse.model';
import { AirConditionerService } from 'src/app/service/air-conditioner.service';

@Component({
  selector: 'app-washing-machine-history',
  templateUrl: './air-condition-history.component.html',
  styleUrls: ['./air-condition-history.component.css']
})
export class AirConditionHistoryComponent implements OnInit{

  public fetchedData:Array<AirConditionerHistoryResponse> = []
  public dataToDisplay:Array<AirConditionerHistoryResponse> = []

  
  public isNextButtonVisible:boolean = true;
  public isPreviousButtonVisible:boolean = false;
  
  public currentPage:number = 0 
  public isLastPage:boolean = false;
  
  public deviceId:number = 0;
  public numberOfRowsPerPage:number = 20
  
  public isDeviceOn:boolean = false;
  
  public isFilterOpen: boolean = false;
  
  @ViewChild(AirConditionFilterComponent) filterForm:AirConditionFilterComponent = {} as AirConditionFilterComponent

  // constructor() {
  //   this.initTestData()
  // }

  constructor(private route: ActivatedRoute, private airConditionerService: AirConditionerService) {
    this.route.params.subscribe(params => {
      const id = params['deviceId'];
      this.deviceId = parseInt(id, 10)
    });
  }
  
  ngOnInit(): void {
    this.airConditionerService.getHistory(this.deviceId, 
      {pageNumber:0, pageSize:this.numberOfRowsPerPage}).subscribe(response => {
        response.map(el => {
        let date = new Date(el.timestamp);
        el.timestamp = date.toLocaleString();
      })
      this.dataToDisplay = response;
      this.fetchedData = response;

      if (this.dataToDisplay.length < this.numberOfRowsPerPage) this.isLastPage = true;
    }) 
    // this.initTestData()
  }

  
  public initTestData():void {
    let testData:AirConditionerHistoryResponse = {} as AirConditionerHistoryResponse
    testData.executor = "AIR_CONDITIONER";
    testData.action = "ON_PERIODIC_HEATING";
    testData.temperature = 20;
    testData.timestamp = Date.now().toLocaleString();

    for(let i=0; i<20; i++){
      this.dataToDisplay.push(testData);
      this.fetchedData.push(testData);
    }
      if(this.dataToDisplay.length < this.numberOfRowsPerPage) this.isLastPage = true;
  }

  public nextPage():void {
    this.currentPage++;
    this.airConditionerService.getHistory(this.deviceId, 
      {pageNumber:this.currentPage, pageSize:this.numberOfRowsPerPage})
      .subscribe(response => {
      if(response.length === 0) {
        this.isLastPage = true
        return;
      }

      response.map(el => {
        let date = new Date(el.timestamp);
        el.timestamp = date.toLocaleString();
      })
      
      this.dataToDisplay = response;
      this.fetchedData = response;
    }) 
  }

  public previousPage():void {
    if(this.isFirstPage()) return
    this.currentPage--;

    this.airConditionerService.getHistory(this.deviceId, 
      {pageNumber:this.currentPage, pageSize:this.numberOfRowsPerPage})
      .subscribe(response => {

      response.map(el => {
        let date = new Date(el.timestamp);
        el.timestamp = date.toLocaleString();
      })
      
      this.dataToDisplay = response;
      this.fetchedData = response;
    }) 
  }

  public isFirstPage():boolean {
    return this.currentPage === 0
  }


  public openFilterForm():void {
    this.isFilterOpen = true;
  }

  public closeFilterForm():void {
    this.isFilterOpen = false;
  }

  public applyFilter(event: { userEmail: string, startDate: string, endDate: string }):void {
    console.log('Received event data:', event);

    this.dataToDisplay = this.fetchedData.filter(item => {
    const isUserMatch = event.userEmail ? item.executor === event.userEmail : true;
    const isDateMatch = this.isDateBetween(item.timestamp, event.startDate, event.endDate)

    return isUserMatch && isDateMatch
  });

    this.isFilterOpen = false;
  }

  public toggleDevice(event:boolean):void {
    this.isDeviceOn = event
    
    // TODO ako imam vremena
    // TODO implement logic for turning off device
    // TODO implement logic for turning on device 
  }

  isDateBetween(dateToCheck:string, startDate:string, endDate:string):boolean {
    const date = new Date(dateToCheck);
    const start = new Date(startDate);
    const end = new Date(endDate);

    return date >= start && date <= end;
  }
}
