import React from 'reactn';
import { appStorage } from './app-storage';

export class GlobalState {
  public get networkKey(): string | null | undefined {
    return React.getGlobal<GlobalState>().networkKey;
  }

  public async setNetworkKey(networkKey: string) {
    await React.setGlobal<GlobalState>({
      networkKey,
    });
    await appStorage.setNetworkKey(networkKey);
  }

  public async initialize() {
    await React.setGlobal<GlobalState>({
      networkKey: appStorage.networkKey,
    });
  }
}

export const globalState: GlobalState = new GlobalState();
