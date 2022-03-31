class LockAccessPolicy < ApplicationPolicy
  def initialize(user, lock_access)
    @user = user
    @lock_access = lock_access
    @lock = user.locks.find_by_id(lock_access.lock_id)
  end

  def index?
    # only allow if user can see the lock
    !@lock.nil?
  end

  def show?
    index?
  end

  def create?
    # allow if user is owner or manager of the lock
    # here lock_access is a new empty instance
    user_requesting = @lock&.lock_accesses&.find_by_user_id(@user.id)
    user_requesting&.manager? || user_requesting&.owner?
  end

  def update?
    create?
  end

  def destroy?
    create?
  end
end
