import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class HomeBatteryWebSocketService {

  private sockets: Map<number, WebSocket> = new Map();

  constructor() {}

  connect(propertyId: number, onMessage: (message: string) => void): void {
    const socket: WebSocket = new WebSocket(`${environment.socketUrl}/home-battery-measurements?propertyId=${propertyId}`);
    this.sockets.set(propertyId, socket);

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

  closeConnection(propertyId: number, reason: string = "No reason...") {
    const socket = this.sockets.get(propertyId);
    if (socket != undefined) {
      socket?.close(1000, reason);
    }
  }
}
