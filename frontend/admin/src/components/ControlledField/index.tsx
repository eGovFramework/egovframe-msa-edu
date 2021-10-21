import { Control, FormState, UseFormGetValues } from 'react-hook-form'

export * from './ControlledDateRangePicker'
export * from './ControlledRadioField'
export * from './ControlledSwitchField'
export * from './ControlledTextField'

export interface ControlledFieldProps {
  control: Control<any, object>
  formState: FormState<any>
  getValues?: UseFormGetValues<any>
}
