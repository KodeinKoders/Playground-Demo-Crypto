import Foundation


@objc public class DataResult: NSObject {
    
    @objc public private(set) var success: Data?
    @objc public private(set) var failure: Error?
    
    private init(_ success: Data?, _ failure: Error?) {
        super.init()
        self.success = success
        self.failure = failure
    }
    
    public convenience init(success: Data) {
        self.init(success, nil)
    }
    public convenience init(failure: Error) {
        self.init(nil, failure)
    }
    public convenience init(_ block: () throws -> Data) {
        do {
            let data = try block()
            self.init(success: data)
        } catch {
            self.init(failure: error)
        }
    }
}
