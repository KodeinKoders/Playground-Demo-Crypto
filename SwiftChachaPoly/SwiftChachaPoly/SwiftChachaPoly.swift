import Foundation
import CryptoKit


@objc public class SwiftChachaPoly : NSObject {
    
    @objc public class func encrypt(key: Data, nonce: Data, aad: Data?, plaintext: Data) -> DataResult {
        DataResult {
            let symKey = SymmetricKey(data: key)
            let chaNonce: ChaChaPoly.Nonce = try ChaChaPoly.Nonce(data: nonce)
            let sealedBox: ChaChaPoly.SealedBox
            if let aad = aad {
                sealedBox = try ChaChaPoly.seal(plaintext, using: symKey, nonce: chaNonce, authenticating: aad)
            } else {
                sealedBox = try ChaChaPoly.seal(plaintext, using: symKey, nonce: chaNonce)
            }
            return sealedBox.ciphertext + sealedBox.tag
        }
    }
 
    @objc public class func decrypt(key: Data, nonce: Data, authenticatedData: Data, ciphertextAndTag: Data) -> DataResult {
        DataResult {
            let tagIndex = ciphertextAndTag.endIndex - 16
            let ciphertext = ciphertextAndTag[ciphertextAndTag.startIndex ..< tagIndex]
            let tag = ciphertextAndTag[tagIndex ..< ciphertextAndTag.endIndex]
 
            let symKey = SymmetricKey(data: key)
            let chaNonce: ChaChaPoly.Nonce = try ChaChaPoly.Nonce(data: nonce)
            let sealedBox: ChaChaPoly.SealedBox = try ChaChaPoly.SealedBox(nonce: chaNonce, ciphertext :ciphertext, tag: tag)
            return try ChaChaPoly.open(sealedBox, using: symKey, authenticating: authenticatedData)
        }
    }
}
