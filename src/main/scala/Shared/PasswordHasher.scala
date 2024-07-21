package Shared

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher:
  // 常量定义
  private val ALGORITHM = "PBKDF2WithHmacSHA512"
  private val ITERATIONS = 120000
  private val KEY_LENGTH = 256
  private val SALT_LENGTH = 16

  // 生成随机盐
  private def generateSalt(): Array[Byte] =
    val random = new SecureRandom()
    val salt = new Array[Byte](SALT_LENGTH)
    random.nextBytes(salt)
    salt

  // 哈希密码
  def hashPassword(password: String): (String, String) =
    val salt = generateSalt()
    val hash = hashWithSalt(password, salt)
    (Base64.getEncoder.encodeToString(hash), Base64.getEncoder.encodeToString(salt))

  // 使用给定的盐对密码进行哈希
  private def hashWithSalt(password: String, salt: Array[Byte]): Array[Byte] =
    val spec = new PBEKeySpec(password.toCharArray, salt, ITERATIONS, KEY_LENGTH)
    val factory = SecretKeyFactory.getInstance(ALGORITHM)
    factory.generateSecret(spec).getEncoded

  // 验证密码
  def verifyPassword(password: String, storedHash: String, storedSalt: String): Boolean =
    val salt = Base64.getDecoder.decode(storedSalt)
    val computedHash = hashWithSalt(password, salt)
    Base64.getEncoder.encodeToString(computedHash) == storedHash
