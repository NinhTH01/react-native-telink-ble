import { uint16ToHexString } from '../helpers/native';

describe('native functions', () => {
  it('to short', () => {
    expect(uint16ToHexString(0xffff)).toEqual('ff ff');
  });
});
