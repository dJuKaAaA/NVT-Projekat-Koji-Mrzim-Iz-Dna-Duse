import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { WashingMachineHistoryResponse } from 'src/app/model/washing-machine/response/washing-m-history-response.model';
import { WashingMachineService } from 'src/app/service/washing-machine.service';
import { WashingMachineMoreInfoFilterComponent } from '../washing-machine-more-info-filter/washing-machine-more-info-filter.component';

@Component({
  selector: 'app-washing-machine-history',
  templateUrl: './washing-machine-history.component.html',
  styleUrls: ['./washing-machine-history.component.css']
})
export class WashingMachineHistoryComponent implements OnInit{
  
  public fetchedData:Array<WashingMachineHistoryResponse> = []
  public dataToDisplay:Array<WashingMachineHistoryResponse> = []

  
  public isNextButtonVisible:boolean = true;
  public isPreviousButtonVisible:boolean = false;
  
  public currentPage:number = 0 
  public isLastPage:boolean = false;
  
  public deviceId:number = 0;
  public numberOfRowsPerPage:number = 20
  
  public isDeviceOn:boolean = false;
  
  public isFilterOpen: boolean = false;

  @ViewChild(WashingMachineMoreInfoFilterComponent) filterForm:WashingMachineMoreInfoFilterComponent = {} as WashingMachineMoreInfoFilterComponent
  
    constructor(private route: ActivatedRoute, private washingMachineService: WashingMachineService) {
    this.route.params.subscribe(params => {
      const id = params['deviceId'];
      this.deviceId = parseInt(id, 10)
    });
  }

  ngOnInit(): void {
    this.washingMachineService.getHistory(this.deviceId, 
      {pageNumber:0, pageSize:this.numberOfRowsPerPage}).subscribe(response => {
        response.map(el => {
        let date = new Date(el.timestamp);
        el.timestamp = date.toLocaleString();
      })
      this.dataToDisplay = response;
      this.fetchedData = response;

      if (this.dataToDisplay.length < this.numberOfRowsPerPage) this.isLastPage = true;
    }) 
  }

  public nextPage():void {
    this.currentPage++;
    this.washingMachineService.getHistory(this.deviceId, 
      {pageNumber:this.currentPage, pageSize:this.numberOfRowsPerPage})
      .subscribe(response => {
      if (response.length === 0) {
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
    if (this.isFirstPage()) return
    this.currentPage--;
     this.washingMachineService.getHistory(this.deviceId, 
      {pageNumber:this.currentPage, pageSize:this.numberOfRowsPerPage})
      .subscribe(response => {
        response.map(el => {
        let date = new Date(el.timestamp);
        el.timestamp = date.toLocaleString();
      })
      
      this.dataToDisplay = response;
      this.fetchedData = response;
      });
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
      return event.userEmail === item.executor;
  });

    this.dataToDisplay = this.dataToDisplay.filter(item => {
      console.log(item.timestamp);
      return this.isDateBetween(item.timestamp, event.startDate, event.endDate);
    })
    this.isFilterOpen = false;
  }

  isDateBetween(dateToCheck:string, startDate:string, endDate:string):boolean {
    const date = new Date(dateToCheck);
    const start = new Date(startDate);
    const end = new Date(endDate);

    console.log(start)
    console.log(date)
    console.log(end)

    return date >= start && date <= end;
  }

}
