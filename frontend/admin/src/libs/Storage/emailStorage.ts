import { loginFormType } from '@components/Auth/LoginForm'
import { LocalStorageWorker } from './index'

// custom class for store emails in local storage
export class EmailStorage {
  private storageWorker: LocalStorageWorker

  // main key
  private storageKey: string

  // login info data
  private loginInfo: loginFormType

  constructor(storageKey: string) {
    this.storageWorker = new LocalStorageWorker()

    this.storageKey = storageKey

    this.loginInfo = { email: null, password: null, isRemember: false }

    this.activate()
  }

  // activate custom storage for login info
  activate() {
    this.load()
  }

  load() {
    var storageData = this.storageWorker.get(this.storageKey)

    if (storageData != null && storageData.length > 0) {
      var info = JSON.parse(storageData)
      if (info) {
        this.loginInfo = info
      }
    }
  }

  get() {
    return this.loginInfo
  }

  // add new email (without duplicate)
  set(info: loginFormType) {
    if (info.isRemember) {
      this.loginInfo = info
      //  save to storage
      this.save()
    } else {
      this.clear()
    }
  }

  // clear all data about login info
  clear() {
    // remove with key
    this.storageWorker.remove(this.storageKey)
  }

  // save to storage (save as JSON string)
  save() {
    var jsonInfo = JSON.stringify(this.loginInfo)
    this.storageWorker.add(this.storageKey, jsonInfo)
  }
}
