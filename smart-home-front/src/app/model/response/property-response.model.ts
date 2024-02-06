import { ImgRequest } from "../request/img-request.model"
import { CityRequestDto } from "../request/city-country-request.model"
import { PropertyType } from "../request/property-request.model"

export interface PropertyResponseDto {
    id: number
    name: string
    ownerEmail: string
    floors: number
    area: number
    address: string
    city: CityRequestDto
    type: PropertyType
    image: ImgRequest
    status: PropertyStatus
}

export enum PropertyStatus {PENDING, APPROVED, DENIED}