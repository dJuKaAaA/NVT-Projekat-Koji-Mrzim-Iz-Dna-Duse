import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WashingMachineCommand } from 'src/app/model/washing-machine/constants/washing-m-command-enum';
import { WashingMachineCurrentWorkMode } from 'src/app/model/washing-machine/constants/washing-m-current-work-mode-enum';
import { WashingMachineCancelAppointmentRequest } from 'src/app/model/washing-machine/request/washing-m-cancel-appintment-request.model';
import { WashingMachineSetWorkModeRequest } from 'src/app/model/washing-machine/request/washing-m-set-work-mode-request.model';
import { WashingMachineAppointmentRequest } from 'src/app/model/washing-machine/request/washing-m-work-appointment-request.model';
import { WashingMachineAppointmentResponse } from 'src/app/model/washing-machine/response/washing-m-appointment-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { WashingMachineService } from 'src/app/service/washing-machine.service';

@Component({
  selector: 'app-washing-machine-manager',
  templateUrl: './washing-machine-manager.component.html',
  styleUrls: ['./washing-machine-manager.component.css']
})
export class WashingMachineManagerComponent implements OnInit{

  appointments:WashingMachineAppointmentResponse[] = []

  isFormOpen:boolean = false;
  deviceId:number = 1

  currentWorkMode:string;

  // Promenljiva za odmah postavljanje radnog režima
  immediateWorkMode: string;  

  scheduledStartTime:string;
  scheduledEndTime:string;
  scheduledWorkMode: string = "SCHEDULED_STANDARD_WASH_PROGRAM";
  
  constructor(
    private router: Router,
    private activateRoute: ActivatedRoute,
    private washinMachineService: WashingMachineService,
    private authService:AuthService) {
        this.activateRoute.params.subscribe(params => {
        const id = params['deviceId'];
        this.deviceId = parseInt(id, 10)});
    }


  public ngOnInit(): void {
    this.washinMachineService.getById(this.deviceId).subscribe( response =>
      {
        console.log(response);
        this.appointments = response.workPlan;
        this.currentWorkMode = response.workMode;
        if (this.currentWorkMode != "OFF") {
          this.immediateWorkMode = this.currentWorkMode;
        } else {
          this.immediateWorkMode = "STANDARD_WASH_PROGRAM";
        }
        console.log(this.currentWorkMode);
      }
    )
  }

  public openScheduleForm():void {
    this.isFormOpen = true;
  }

  public closeScheduledForm():void {
    this.isFormOpen = false;
  }

  public openHistoryPage():void {
    this.router.navigate(['/washing-machine-history',this.deviceId]);
  }

  public setCurrentWorkMode():void {
    if (this.immediateWorkMode == 'OFF') {
      alert('You have to set program!')
      return;
    }
    let request:  WashingMachineSetWorkModeRequest = {
      setByUserEmail: this.authService.getEmail(),
      workMode: this.stringToWorkMode(this.immediateWorkMode)
    }
    console.log(request)
    this.washinMachineService.setCurrentWorkMode(this.deviceId, request).subscribe(
      {
        next: response => { 
          this.currentWorkMode = this.immediateWorkMode;
          alert("You have successfully changed work mode!")
        },
        error: (err:HttpErrorResponse) => alert(err.error.message)
      }
    )
  }

  public scheduleAppointment():void {

    let appointment:WashingMachineAppointmentRequest = {
      bookedByEmail:this.authService.getEmail(),
      startTime:this.scheduledStartTime,
      command:this.stringToAirConditionerCommand(this.scheduledWorkMode)
    }
    this.washinMachineService.schedule(this.deviceId, appointment).subscribe({
      next: response => {
        alert("you have successfully created work appointment!")
        this.appointments.push(response)
        this.isFormOpen = false;
      },
      error: (err:HttpErrorResponse) => alert(err.error.message)
    })
  }

  public cancelAppointment(appointmentId:number): void {
    let request:WashingMachineCancelAppointmentRequest = {
      canceledByEmail: this.authService.getEmail()
    }
    this.washinMachineService.cancelAppointment(this.deviceId, appointmentId, request).subscribe(
      {
        next:response => {
          this.appointments = this.appointments.filter(el => el.id !== appointmentId)
          console.log(this.appointments);
          alert('You have successfully canceled appointment!')
        },
        error: (err:HttpErrorResponse) => alert(err.error.message)
      })
  }

  private stringToWorkMode(value: string): WashingMachineCurrentWorkMode  {
    return value as WashingMachineCurrentWorkMode;
  }

  stringToAirConditionerCommand(value: string): WashingMachineCommand  {
    return value as WashingMachineCommand;
  }
}
