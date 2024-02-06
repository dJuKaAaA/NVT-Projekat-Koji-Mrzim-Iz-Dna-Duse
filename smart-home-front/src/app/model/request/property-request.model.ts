import { ImgRequest } from "./img-request.model";
import { CityRequestDto } from "./city-country-request.model"

export interface PropertyRequestDto {
    name: string
    ownerEmail: string
    floors: number
    area: number
    longitude: number
    latitude: number
    address: string
    city: CityRequestDto
    type: PropertyType
    image: ImgRequest
}

export interface PropertyStatusRequestDto {
    id: number
    isApproved: Boolean
    denialReason: string | null
}

export enum PropertyType {HOUSE, APARTMENT}