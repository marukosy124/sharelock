class User < ApplicationRecord
  has_many :lock_accesses
  has_many :locks, through: :lock_accesses

  def self.first_or_create_by_email(email)
    user = User.find_by(name: email)
    return user unless user.nil?

    url = 'https://identitytoolkit.googleapis.com/v1/accounts:signUp'
    new_user_call = HTTParty.post(url, headers: { 'Content-Type' => 'application/x-www-form-urlencoded' },
                                       body: URI.encode_www_form({
                                                                   'key' => ENV['FIREBASE_AUTH_API_KEY'],
                                                                   'email' => email,
                                                                   'password' => 'hahalol'
                                                                 }))

    if new_user_call.response.code == '200'
      user_info = new_user_call.parsed_response
      user_id = user_info['localId']
      User.create(id: user_id, name: email)

    else
      puts new_user_call.response.body
      raise 'Error Firebase Account Creation'
    end
  end
end
