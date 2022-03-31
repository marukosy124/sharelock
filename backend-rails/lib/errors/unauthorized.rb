module Errors
  class Unauthorized < StandardError
    def initialize
      super(
        title: 'Unauthorized',
        status: 401,
        detail: message || 'Invalid Token',
        source: { pointer: '/request/headers/authorization' }
      )
    end
  end
end
