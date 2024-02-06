import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ElectricVehicleChargerSocketService {

  private sockets: Map<string, WebSocket> = new Map();

  constructor() {}

  connect(deviceId: number, chargingVehicleId: number, onMessage: (message: string) => void): void {
    const socket: WebSocket = new WebSocket(`${environment.socketUrl}/electric-vehicle-charger-measurements?deviceId=${deviceId}&chargingVehicleId=${chargingVehicleId}`);
    this.sockets.set(deviceId + " " + chargingVehicleId, socket);

    socket.onopen = (event) => {
      console.log('WebSocket connection opened:', event);
    };

    socket.onmessage = (event) => {
      onMessage(event.data);
    };

    socket.onclose = (event) => {
      console.log('WebSocket connection closed:', event);
      console.log('Code: ', event.code);
      console.log('Reason: ', event.reason);
    };
  }

  closeConnection(deviceId: number, chargingVehicleId: number, reason: string = "No reason...") {
    const socket = this.sockets.get(deviceId + " " + chargingVehicleId);
    if (socket != undefined) {
      socket?.close(1000, reason);
    }
  }

}
