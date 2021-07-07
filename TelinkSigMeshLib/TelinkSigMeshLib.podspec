require "json"

package = JSON.parse(File.read(File.join(__dir__, "..", "package.json")))

Pod::Spec.new do |s|
  s.name          = "TelinkSigMeshLib"
  s.version       = "3.3.2"
  s.summary       = package["description"]
  s.homepage      = package["homepage"]
  s.license       = package["license"]
  s.authors       = package["author"]

  s.platforms     = { :ios => "10.0" }
  s.source        = { :git => "https://github.com/thanhtunguet/react-native-telink-ble.git", :tag => "#{s.version}" }

  s.source_files  = "TelinkSigMeshLib/**/*.{h,m,mm,swift}"
  
  s.prefix_header_file = 'TelinkSigMeshLib/TelinkSigMeshLibPrefixHeader.pch'
  s.ios.framework      = 'CoreBluetooth'
  s.dependency           "OpenSSL-Universal"
end
