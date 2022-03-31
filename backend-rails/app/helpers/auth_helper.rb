module AuthHelper
  def current_user
    $firebase_current_user
  end

  def use_auth
    auth_field = request.authorization
    unless auth_field
      return render json: { :message => "No Token" }, status: :unauthorized
    end

    _, token = request.authorization.split(' ')
    firebase_verification(token)
  end

  def firebase_verification(token)
    url = 'https://www.googleapis.com/identitytoolkit/v3/relyingparty/getAccountInfo?key=' + ENV['FIREBASE_AUTH_API_KEY']
    firebase_verification_call = HTTParty.post(url, headers: { 'Content-Type' => 'application/json' },
                                                    body: { 'idToken' => token }.to_json)

    if firebase_verification_call.response.code == '200'
      firebase_infos = firebase_verification_call.parsed_response
      local_id = firebase_infos['users'][0]['localId']

      email = firebase_infos['users'][0]['email']
      username = email || "unknown"

      $firebase_current_user = User.where(id: local_id).first_or_create
      $firebase_current_user.name = username
      $firebase_current_user.save

    else
      render json: firebase_verification_call.response.body, status: :unauthorized
    end
  end
end
