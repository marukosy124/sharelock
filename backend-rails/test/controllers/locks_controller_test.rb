require "test_helper"

class LocksControllerTest < ActionDispatch::IntegrationTest
  setup do
    @lock = locks(:one)
  end

  test "should get index" do
    get locks_url, as: :json
    assert_response :success
  end

  test "should create lock" do
    assert_difference("Lock.count") do
      post locks_url, params: { lock: { name: @lock.name, private_key: @lock.private_key } }, as: :json
    end

    assert_response :created
  end

  test "should show lock" do
    get lock_url(@lock), as: :json
    assert_response :success
  end

  test "should update lock" do
    patch lock_url(@lock), params: { lock: { name: @lock.name, private_key: @lock.private_key } }, as: :json
    assert_response :success
  end

  test "should destroy lock" do
    assert_difference("Lock.count", -1) do
      delete lock_url(@lock), as: :json
    end

    assert_response :no_content
  end
end
