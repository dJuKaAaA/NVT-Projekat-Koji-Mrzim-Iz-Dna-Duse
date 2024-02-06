import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AirConditionerCommand } from 'src/app/model/air_conditioner/constants/air-conditioner-command-enum';
import { AirConditionerCurrentWorkMode } from 'src/app/model/air_conditioner/constants/air-conditioner-current-work-mode-enum';
import { AirConditionerAppointmentRequest } from 'src/app/model/air_conditioner/request/air-conditioner-appointment-request.model';
import { AirConditionerSetWorkModeRequest } from 'src/app/model/air_conditioner/request/air-conditioner-set-work-mode-request.model';
import { AirConditionerCancelAppointmentRequest } from 'src/app/model/air_conditioner/request/cancel-air-conditioner-appointment-request.model';
import { AirConditionerAppointmentResponse } from 'src/app/model/air_conditioner/response/air-conditioner-appointment-response.model';
import { AirConditionerService } from 'src/app/service/air-conditioner.service';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-air-conditioner-manager',
  templateUrl: './air-conditioner-manager.component.html',
  styleUrls: ['./air-conditioner-manager.component.css']
})
export class AirConditionerManagerComponent implements OnInit{


  appointments:AirConditionerAppointmentResponse[] = []

  isFormOpen:boolean = false;
  deviceId:number = 1

  currentWorkTemperature: Number;
  currentWorkMode:string;

  immediateTemperature:number;
  immediateWorkMode: string;  // Promenljiva za odmah postavljanje radnog režima

  scheduledStartTime:string;
  scheduledEndTime:string;
  scheduledWorkMode: string;
  scheduledTemperature:number



  constructor(
    private router: Router,
    private activateRoute: ActivatedRoute,
    private airConditionerService: AirConditionerService,
    private authService:AuthService) {
        this.activateRoute.params.subscribe(params => {
        const id = params['deviceId'];
        this.deviceId = parseInt(id, 10)});
    }

    public ngOnInit(): void {
      this.airConditionerService.getById(this.deviceId).subscribe(response => {
        this.appointments = response.workPlan;
        this.currentWorkTemperature = response.currentWorkTemperature;
        this.currentWorkMode = response.workMode;
        this.immediateWorkMode = this.currentWorkMode;
        
        if (this.currentWorkMode == "OFF") {
            this.currentWorkTemperature = NaN;
        } 
      })
    }

  public onModeChange() {
    if(this.immediateWorkMode == 'OFF')
      this.immediateTemperature = Number(null);
  }

  public openScheduleForm():void {
    this.isFormOpen = true;
  }

  public closeScheduledForm():void {
    this.isFormOpen = false;
  }

  public openHistoryPage():void {
    this.router.navigate(['/air-conditioner-history',this.deviceId]);
  }

  public setCurrentWorkMode():void {

    let request:  AirConditionerSetWorkModeRequest = {
      setByUserEmail: this.authService.getEmail(),
      wantedTemperature: this.immediateTemperature,
      workMode: this.stringToWorkMode(this.immediateWorkMode)
    }
    console.log(request)
    this.airConditionerService.setCurrentWorkMode(this.deviceId,request).subscribe(
      {
        next: response => { alert("You have successfully changed work mode!")},
        error: (err:HttpErrorResponse) => alert(err.error.message)
      }
    )
  }

  public scheduleAppointment():void {

    let appointment:AirConditionerAppointmentRequest = {
      bookedByEmail:this.authService.getEmail(),
      startTime:this.scheduledStartTime,
      endTime:this.scheduledEndTime,
      wantedTemperature: this.scheduledTemperature,
      command:this.stringToAirConditionerCommand(this.scheduledWorkMode)
    }
    this.airConditionerService.schedule(this.deviceId, appointment).subscribe({
      next: response => {
        alert("you have successfully created work appointment!")
        this.appointments.push(response)
        this.isFormOpen = false;
      },
      error: (err:HttpErrorResponse) => alert(err.error.message)
    })
  }

  public cancelAppointment(appointmentId:number): void {
    let request:AirConditionerCancelAppointmentRequest = {
      canceledByEmail: this.authService.getEmail()
    }
    this.airConditionerService.cancelAppointment(this.deviceId, appointmentId, request).subscribe(
      {
        next:response => {
          this.appointments = this.appointments.filter(el => el.id !== appointmentId)
          console.log(this.appointments);
          alert('You have successfully canceled appointment!')
        },
        error: (err:HttpErrorResponse) => alert(err.error.message)
      })
  }

  private stringToWorkMode(value: string): AirConditionerCurrentWorkMode  {
    return value as AirConditionerCurrentWorkMode;
  }

  stringToAirConditionerCommand(value: string): AirConditionerCommand  {
    return value as AirConditionerCommand;
  }

}
