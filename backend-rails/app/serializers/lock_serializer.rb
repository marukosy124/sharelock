class LockSerializer < ActiveModel::Serializer
  attributes :id, :name
  attribute :token, unless: -> { object.token.nil? }

  has_many :lock_accesses
end
